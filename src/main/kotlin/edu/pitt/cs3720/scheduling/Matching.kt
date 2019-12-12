package edu.pitt.cs3720.scheduling

import edu.pitt.cs3720.scheduling.framework.*
import edu.pitt.cs3720.scheduling.framework.des.Event
import edu.pitt.cs3720.scheduling.framework.util.CircularBuffer
import edu.pitt.cs3720.scheduling.framework.util.SortedArrayList
import edu.pitt.cs3720.scheduling.framework.util.duplicate
import java.util.concurrent.TimeUnit
import kotlin.math.floor


class MatchingScheduler(history: Int = 10): Scheduler() {

    private val availableDevices = SortedArrayList<Device>(compareBy { it.capability }) // Goodness I <3 Kt.
    private val jobSizeTracker = CircularBuffer(capacity = history)


    override fun deviceOnline(device: Device) {
        availableDevices.add(device)
    }

    override fun deviceOffline(device: Device, job: Job?) {
        availableDevices.remove(device)
    }

    override fun deviceLost(device: Device, job: Job?) {
        availableDevices.remove(device)
    }

    override fun jobAdded(job: Job) {
        jobSizeTracker.add(job.size)
    }

    override fun scheduleWork() {
        val trackedJobSizes = jobSizeTracker.sorted()
        // Lets give the biggest job to the most capable device until we have collected enough history data
        if (trackedJobSizes.size < jobSizeTracker.capacity/2) {
            val sortedJobs = jobs.duplicate().sortedBy { it.size }.reversed()
            for (job in sortedJobs) {
                scheduleJobOnFirstAvailableDevice(job)
            }
        } else {
            // I don't want no concurrent modification exceptions, dup just to be on the safe side
            for (job in jobs.duplicate()) {
                var jobPlacement = 0
                // Find where in the list of tracked jobs this one would fall into
                for (i in 0 until trackedJobSizes.size) {
                    if (trackedJobSizes[i] < job.size) {
                        jobPlacement = i
                        break
                    } else if (i == trackedJobSizes.size-1) {
                        jobPlacement = i
                    }
                }
                // And calculate a rank to look for a suitable device
                val rank = jobPlacement.toFloat()/trackedJobSizes.size
                val availableStart = floor(rank*availableDevices.size).toInt()
                // We're always gonna look for a device higher in capability than the placement
                for (i in availableStart until availableDevices.size) {
                    if (availableDevices[i].isIdle()) {
                        schedule(job, availableDevices[i])
                        break
                    }
                }
            }
        }
    }

    private fun scheduleJobOnFirstAvailableDevice(job: Job) {
        for (device in availableDevices) {
            if (device.isIdle()) {
                schedule(job, device)
                break
            }
        }
    }
}

class MatchingSimulation: Simulation() {
    private val scheduler = MatchingScheduler()
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
