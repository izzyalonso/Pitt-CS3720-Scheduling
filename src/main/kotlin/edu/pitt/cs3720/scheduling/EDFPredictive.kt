package edu.pitt.cs3720.scheduling

import edu.pitt.cs3720.scheduling.framework.*
import edu.pitt.cs3720.scheduling.framework.des.Controller
import edu.pitt.cs3720.scheduling.framework.des.Event
import edu.pitt.cs3720.scheduling.framework.util.SortedArrayList
import edu.pitt.cs3720.scheduling.framework.util.duplicate
import java.util.concurrent.TimeUnit


class EDFPredictiveScheduler: Scheduler() {
    private val estimatedCompletionTimes = mutableMapOf<Device, Long>()
    private val failureRates = mutableMapOf<Device, FailureRateTracker>()
    private val availableDevices = SortedArrayList<Device>(Comparator { lhs, rhs ->
        // Sorted in decreasing order of capability
        when {
            lhs.capability < rhs.capability -> 1
            lhs.capability > rhs.capability -> -1
            else -> 0
        }
    })


    override fun deviceOnline(device: Device) {
        availableDevices.add(device)
        if (!failureRates.containsKey(device)) {
            failureRates[device] = FailureRateTracker()
        }
    }

    override fun deviceOffline(device: Device, job: Job?) {
        availableDevices.remove(device)
        if (job != null) failureRates[device]?.incrementFailed()
    }

    override fun deviceLost(device: Device, job: Job?) {
        availableDevices.remove(device)
        if (job != null) failureRates[device]?.incrementFailed()
    }

    override fun scheduleWork() {
        // We are going to create a rank of suitable available devices for every job given a few metrics:
        //  + Deadline: we want to try to schedule jobs with earlier deadlines first
        //  + Size: we want to get as much work done as we possibly can
        //  + Reliability: we'd like to schedule more work on more reliable devices
        //  + How long it will take in idle vs non-idle devices: does it make sense to wait until non-idle devices become idle?
        val jobDeviceMap = mutableMapOf<Job, MutableList<Device>>()

        val jobsDupe = jobs.duplicate().sortedBy { it.deadline }
        for (job in jobsDupe) {
            // The 200 here is to account for RTTs and such
            val requiredCapability = job.size / (job.deadline-Controller.currentTimeMillis()+200)
            jobDeviceMap[job] = mutableListOf<Device>()
            for (device in availableDevices) {
                // We want to filter out devices that would miss the deadline if we wait until they become idle
                if (!device.isIdle()) {
                    val skip = estimatedCompletionTimes[device]?.let { idleTime ->
                        device.capability < job.size / (job.deadline - idleTime + 200)
                    } ?: throw RuntimeException("isIdle() and ETA tracking disagree") // <- this shouldn't happen
                    if (skip) continue
                }
                // We only want to schedule in devices that are capable enough
                // This is marginally better than a filter because we know the list is ordered
                // We could do binary search, but I can't be bothered; this is just a simulation
                // Anywho, if the device doesn't meet the required capability we know the rest of them won't
                if (device.capability < requiredCapability) {
                    break
                }
                jobDeviceMap[job]?.add(device)
            }
            jobDeviceMap[job]?.sortWith(DeviceRank(job))
        }

        // We schedule prioritizing rank choices for jobs with the earliest deadline
        for (job in jobsDupe) {
            jobDeviceMap[job]?.let { devices ->
                // There may not be enough devices to assign jobs to
                if (devices.isNotEmpty()) {
                    val preferredDevice = devices[0]
                    // We can only schedule if the device is idle
                    if (preferredDevice.isIdle()) {
                        schedule(job, preferredDevice)
                        failureRates[preferredDevice]?.incrementAssigned()
                        estimatedCompletionTimes[preferredDevice] = Controller.currentTimeMillis() + job.size / preferredDevice.capability
                    }
                    // We're trying to remove the device from all other jobs' priority lists
                    // This is because we're reserving that device for this job
                    // Can't come up with a better name for these
                    for (job2 in jobsDupe) {
                        jobDeviceMap[job2]?.remove(preferredDevice)
                    }
                }
            }
        }
    }

    /**
     * Compares devices to run a job in.
     */
    inner class DeviceRank(private val job: Job): Comparator<Device> {
        override fun compare(lhs: Device, rhs: Device) = rank(lhs).compareTo(rank(rhs))

        /**
         * Comes up with a rank for a [device]. The smaller the better.
         */
        private fun rank(device: Device): Int {
            if (!device.isIdle()) return Int.MAX_VALUE
            if (Controller.currentTimeMillis() + job.size / device.capability > job.deadline) return Int.MAX_VALUE

            // Primary metric is the capability of the device. We want just enough capable devices to schedule first
            val capability = device.capability

            // Secondary to that is the device failure rate devices with higher failure rates will get penalized
            // We don't want to take this number very seriously until we have gathered enough data though
            //  just in case a device got unlucky in the first dice roll
            return failureRates[device]?.let { tracker ->
                if (tracker.totalJobsAssigned < 10) {
                    val failureRate = if (1-tracker.failureRate() == 0f) {
                        // Next best thing; avoid dividing by zero, avoid over penalizing
                        0.9f
                    } else {
                        tracker.failureRate()
                    }
                    // A failure rate of 0.9 should produce a smaller multiplicative penalty factor than 0.1
                    val inversePenaltyFactor = 1/(1-failureRate)
                    // We care to reduce the factor on top of the unit
                    // For instance: for a failure rate of 0.1, we get a factor of 1.1, but we're only interested in
                    //  mitigating the 0.1 on top of the 1 due to a lack of history; it'd be unfair to divide 1.1 by 10
                    val adjustedInversePenaltyFactor = 1-inversePenaltyFactor
                    // The bigger the history the less we care about mitigating the adjusted value
                    // We get the actual penalty factor by adding the one back to the mitigated value
                    val penaltyFactor = 1 + adjustedInversePenaltyFactor/(10-tracker.totalJobsAssigned)
                    // We mod the ETA accordingly
                    (capability * penaltyFactor).toInt()
                } else {
                    (capability * 1/(1-tracker.failureRate())).toInt()
                }
            } ?: capability
        }
    }

    data class FailureRateTracker(
        var totalJobsAssigned: Int = 0,
        var totalJobsFailed: Int = 0
    ) {
        fun incrementFailed() {
            totalJobsFailed++
        }

        fun incrementAssigned() {
            totalJobsAssigned++
        }

        fun failureRate() = totalJobsFailed.toFloat()/totalJobsAssigned
    }
}



class EDFPredictiveSimulation: Simulation() {
    private val scheduler = EDFPredictiveScheduler()
    private val devices: List<Device>
    private val events: List<Event>

    init {
        devices = listOf(
            Device(scheduler, 1000, 1.1f, 0f, "Phone 1"),
            Device(scheduler, 500, 1f, 0f, "Phone 2"),
            Device(scheduler, 2000, 1.05f, 0.05f, "Phone 3"),
            Device(scheduler, 4000, 1.3f, 0f, "Phone 4"),
            Device(scheduler, 2000, 1.15f, 0.15f, "Phone 5")
        )

        events = listOf(
            awakeEvent(50, devices[0]),
            sleepEvent(TimeUnit.SECONDS.toMillis(10), devices[0]),
            awakeEvent(TimeUnit.SECONDS.toMillis(15), devices[0]),
            awakeEvent(100, devices[1]),
            awakeEvent(150, devices[2]),
            awakeEvent(200, devices[3]),
            awakeEvent(250, devices[4])
        )
    }

    override fun scheduler() = scheduler
    override fun devices() = devices
    override fun setupEvents() = events
}
