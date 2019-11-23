package edu.pitt.cs3720.scheduling.framework

import edu.pitt.cs3720.scheduling.framework.des.Controller
import edu.pitt.cs3720.scheduling.framework.des.EventListener
import edu.pitt.cs3720.scheduling.framework.des.Payload


abstract class Scheduler(private val timeoutMillis: Long): EventListener {
    internal val jobs = mutableListOf<Job>()

    // Who's working on what. Also serves as a device registry
    private val schedule = mutableMapOf<Device, Job?>()
    private val awolDevices = mutableSetOf<Device>()
    private val idleDevices = mutableListOf<Device>()

    // Only one active timeout per device at a time
    private val timeouts = mutableMapOf<Device, Int>()


    /**
     * Adds a job to the queue and schedules work to a device that's idle if there's any
     *
     * @param job the job to add.
     */
    fun addJob(job: Job) {
        jobs.add(job)
        if (idleDevices.isNotEmpty()) {
            scheduleWorkOn(idleDevices.removeAt(0))
        }
    }

    override fun onEvent(payload: Payload) {
        payload.deviceOnline()?.let { deviceOnline ->
            if (schedule.containsKey(deviceOnline.device)) {
                // The device was already registered; it failed and recovered before we could notice
                schedule.remove(deviceOnline.device)?.let { job ->
                    // If it had a job assigned, throw it back to the pool
                    jobs.add(job)
                }
            }
            if (!scheduleWorkOn(deviceOnline.device)) {
                idleDevices.add(deviceOnline.device)
            }
        }
        payload.deviceOffline()?.let { deviceOffline ->
            // Remove a device from the registry and return its job to the lake
            schedule.remove(deviceOffline.device)?.let { job ->
                jobs.add(job)
            }
        }
        payload.workCompleted()?.let { workCompleted ->
            Controller.removeEvent(timeouts[workCompleted.device] ?: -1)
            if (!scheduleWorkOn(workCompleted.device)) {
                idleDevices.add(workCompleted.device)
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
                if (!scheduleWorkOn(statusUpdate.device)) {
                    idleDevices.add(statusUpdate.device)
                }
            }
        }
        payload.statusRequestTimeout()?.let { statusRequestTimeout ->
            val device = statusRequestTimeout.statusRequest.device
            if (awolDevices.contains(device)) {
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
     * Schedules work on a device.
     *
     * @return true if there was work to schedule, false otherwise.
     */
    private fun scheduleWorkOn(device: Device): Boolean {
        if (jobs.isEmpty()) return false

        // Schedule the work
        val nextJob = nextJobFor(device)
        schedule[device] = nextJob
        Controller.registerEvent(
            payload = WorkRequest(device, nextJob),
            listener = device
        )

        // And set up a timeout
        timeouts[device] = Controller.registerEvent(
            millisFromNow = timeoutMillis,
            payload = WorkTimeout(device = device, job = nextJob),
            listener = this
        )

        return true
    }

    /*
     * The following events can be implemented to gather intel about the state of the system.
     */

    fun deviceOnline(device: Device) {

    }

    fun deviceOffline(device: Device) {

    }

    fun deviceLost(device: Device) {

    }

    fun deviceCompletedWork(device: Device, job: Job) {

    }

    /*
     * The following events need to be implemented by the scheduler to run smoothly
     */

    abstract fun addJobToPool(job: Job)
    abstract fun nextJobFor(device: Device): Job
}
