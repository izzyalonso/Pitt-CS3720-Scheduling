package edu.pitt.cs3720.scheduling.framework

import edu.pitt.cs3720.scheduling.framework.des.Controller
import edu.pitt.cs3720.scheduling.framework.des.EventListener
import edu.pitt.cs3720.scheduling.framework.des.Payload
import edu.pitt.cs3720.scheduling.framework.util.duplicate
import java.util.concurrent.TimeUnit


/**
 * Scheduler abstraction. Contains all basic functionality shared across schedulers.
 *
 * @param timeoutMillis a request's timeout. Or how soon the scheduler checks in with the device to see if everything's
 *                      still dandy after scheduling work.
 */
abstract class Scheduler(private val timeoutMillis: Long): EventListener {

    /**
     * Default [timeoutMillis] of 5 seconds. In actuality this should probably be larger.
     */
    constructor(): this(TimeUnit.SECONDS.toMillis(5))

    // It's not the best thing to keep these protected and mutable, but that's okay for now
    protected val jobs = mutableListOf<Job>()
    protected val idleDevices = mutableListOf<Device>()

    // Who's working on what. Also serves as a device registry
    private val schedule = mutableMapOf<Device, Job?>()
    private val awolDevices = mutableSetOf<Device>()

    // Only one active timeout per device at a time
    private val timeouts = mutableMapOf<Device, Int>()


    /**
     * Adds a [job] to the queue and schedules work to a device that's idle if there's any
     */
    fun addJob(job: Job) {
        println("Adding Job$job to pool")
        jobs.add(job)
        jobAdded(job)
        if (idleDevices.isNotEmpty()) {
            internalScheduleWork()
        }
    }

    fun addJobs(newJobs: List<Job>) {
        println("Adding Jobs$newJobs to pool")
        jobs.addAll(newJobs)
        for (job in newJobs) {
            jobAdded(job)
        }
        if (idleDevices.isNotEmpty()) {
            internalScheduleWork()
        }
    }

    final override fun onEvent(payload: Payload) {
        payload.deviceOnline()?.let { deviceOnline ->
            if (schedule.containsKey(deviceOnline.device)) {
                // The device was already registered; it failed and recovered before we could notice
                schedule.remove(deviceOnline.device)?.let { job ->
                    // If it had a job assigned, throw it back to the lake
                    jobs.add(job)
                }
            } else {
                // Only notify if we did notice
                deviceOnline(deviceOnline.device)
            }
            idleDevices.add(deviceOnline.device)
            if (jobs.isNotEmpty()) {
                internalScheduleWork()
            }
        }
        payload.deviceOffline()?.let { deviceOffline ->
            deviceOffline(deviceOffline.device, schedule[deviceOffline.device])
            // Remove a device from the registry and return its job to the lake
            schedule.remove(deviceOffline.device)?.let { job ->
                jobs.add(job)
            }
        }
        payload.workCompleted()?.let { workCompleted ->
            Controller.removeEvent(timeouts[workCompleted.device] ?: -1)
            Analytics.jobCompleted(workCompleted.job, workCompleted.device)
            idleDevices.add(workCompleted.device)
            schedule.remove(workCompleted.device)
            // schedule.remove(workCompleted.device)
            if (jobs.isNotEmpty()) {
                internalScheduleWork()
            }
        }
        payload.workTimeout()?.let { workTimeout ->
            awolDevices.add(workTimeout.device)
            val statusRequest = StatusRequest(workTimeout.device)
            Controller.registerEvent(
                payload = statusRequest,
                listener = workTimeout.device
            )
            timeouts[workTimeout.device] = Controller.registerEvent(
                millisFromNow = 150,
                payload = StatusRequestTimeout(statusRequest),
                listener = this
            )
        }
        payload.statusUpdate()?.let { statusUpdate ->
            awolDevices.remove(statusUpdate.device)
            if (statusUpdate.status.working) {
                Controller.removeEvent(timeouts[statusUpdate.device] ?: -1)
            } else {
                // Something went wrong, we need to reschedule
                schedule.remove(statusUpdate.device)?.let { job ->
                    // If it had a job assigned, throw it back to the pool
                    jobs.add(job)
                }
                idleDevices.add(statusUpdate.device)
                internalScheduleWork()
            }
        }
        payload.statusRequestTimeout()?.let { statusRequestTimeout ->
            val device = statusRequestTimeout.statusRequest.device
            if (awolDevices.contains(device)) {
                deviceLost(device, schedule[device])
                // If we were still having the device under suspicion
                awolDevices.remove(device)
                schedule.remove(device)?.let { job ->
                    // Remove the job it had assigned and throw it back to the pool
                    jobs.add(job)
                }
            }
        }
    }

    /**
     * Performs some checks before requesting the scheduler to do its thing.
     */
    private fun internalScheduleWork() {
        for (job in jobs.duplicate().sortedBy { it.deadline }) {
            // We only consider removing jobs when the deadline has passed
            // At any point in time a mighty device with infinite compute capabilities might join the fleet
            if (job.deadline < Controller.currentTimeMillis()) {
                println("Job$job missed deadline")
                jobs.remove(job)
                Analytics.jobMissedDeadline(job)
            } else {
                // Ordered so we can break; all jobs after this one have not missed their deadline
                break
            }
        }

        if (jobs.isEmpty() or idleDevices.isEmpty()) return

        scheduleWork()
    }

    /**
     * Schedules a [job] on a [device].
     */
    protected fun schedule(job: Job, device: Device) {
        // println("Trying to schedule Job$job on $device")

        if (!jobs.contains(job)) throw IllegalStateException("Job$job not found in job pool")
        if (!idleDevices.contains(device)) throw IllegalStateException("Device$device not found in idle pool")

        // println("Scheduling Job$job on $device")

        jobs.remove(job)
        idleDevices.remove(device)

        schedule[device] = job
        Controller.registerEvent(
            payload = WorkRequest(device, job),
            listener = device
        )

        // And set up a timeout
        timeouts[device] = Controller.registerEvent(
            millisFromNow = timeoutMillis,
            payload = WorkTimeout(device = device, job = job),
            listener = this
        )
    }

    /*
     * The following events can be implemented to gather intel about the state of the system.
     * It's very much self evident when these are called.
     */

    open fun deviceOnline(device: Device) { }
    open fun deviceOffline(device: Device, job: Job?) { }
    open fun deviceLost(device: Device, job: Job?) { }
    open fun jobAdded(job: Job) { }
    open fun deviceCompletedWork(device: Device, job: Job) { }

    /**
     * Gets called when anything significant changes so that the particular implementation of the scheduler we're
     * using can choose what jobs run in what devices.
     */
    abstract fun scheduleWork()

    /**
     * Tells whether the device is idle.
     */
    protected fun Device.isIdle() = idleDevices.contains(this)
}
