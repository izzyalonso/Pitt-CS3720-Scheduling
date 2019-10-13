package edu.pitt.cs3720.scheduling

import edu.pitt.cs3720.scheduling.framework.Controller
import edu.pitt.cs3720.scheduling.framework.EventListener
import edu.pitt.cs3720.scheduling.framework.Payload


abstract class Scheduler(private val jobs: MutableList<Job>, private val timeoutMillis: Int): EventListener {
    // Who's working on what. Also serves as a device registry
    private val schedule = mutableMapOf<Device, Job?>()


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
        payload.timeout()?.let { timeout ->
            /**Controller.addEvent(Event(
                time = event.time + 50,
                payload = StatusRequest(),
                listener = timeout.device
            ))*/
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
            payload = Timeout(device = device, job = nextJob),
            listener = this
        )
    }

    abstract fun nextJobFor(device: Device): Job
}
