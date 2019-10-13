package edu.pitt.cs3720.scheduling

import edu.pitt.cs3720.scheduling.framework.Controller
import edu.pitt.cs3720.scheduling.framework.EventListener
import edu.pitt.cs3720.scheduling.framework.Payload


abstract class Scheduler(private val jobs: MutableList<Job>, private val timeoutMillis: Int): EventListener {
    // Who's working on what. Also serves as a device registry
    private val schedule = mutableMapOf<Device, Job?>()
    private val awolDevices = mutableSetOf<Device>()


    override fun onEvent(payload: Payload) {
        payload.deviceOnline()?.let { deviceOnline ->
            if (schedule.containsKey(deviceOnline.device)) {
                // The device was already registered; it failed and recovered before we could notice
                schedule.remove(deviceOnline.device)?.let { job ->
                    // If it had a job assigned, throw it back to the pool
                    jobs.add(job)
                }
            }
            scheduleWorkOn(deviceOnline.device)
        }
        payload.deviceOffline()?.let { deviceOffline ->
            // Remove a device from the registry and return its job to the lake
            schedule.remove(deviceOffline.device)?.let { job ->
                jobs.add(job)
            }
        }
        payload.workCompleted()?.let { workCompleted ->
            scheduleWorkOn(workCompleted.device)
        }
        payload.workTimeout()?.let { workTimeout ->
            awolDevices.add(workTimeout.device)
            val statusRequest = StatusRequest(workTimeout.device)
            Controller.registerEvent(
                payload = statusRequest,
                listener = workTimeout.device
            )
            Controller.registerEvent(
                millisFromNow = 150,
                payload = StatusRequestTimeout(statusRequest),
                listener = this
            )
        }
        payload.statusUpdate()?.let { statusUpdate ->
            awolDevices.remove(statusUpdate.device)
            if (!statusUpdate.status.working) {
                // Something went wrong, we need to reschedule
                schedule.remove(statusUpdate.device)?.let { job ->
                    // If it had a job assigned, throw it back to the pool
                    jobs.add(job)
                }
                scheduleWorkOn(statusUpdate.device)
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

    private fun scheduleWorkOn(device: Device) {
        // Schedule the work
        val nextJob = nextJobFor(device)
        schedule[device] = nextJob
        Controller.registerEvent(
            payload = WorkRequest(nextJob),
            listener = device
        )

        // And set up a timeout
        Controller.registerEvent(
            millisFromNow = timeoutMillis,
            payload = WorkTimeout(device = device, job = nextJob),
            listener = this
        )
    }

    abstract fun nextJobFor(device: Device): Job
}
