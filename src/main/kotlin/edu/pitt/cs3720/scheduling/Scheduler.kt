package edu.pitt.cs3720.scheduling

import edu.pitt.cs3720.scheduling.framework.Controller
import edu.pitt.cs3720.scheduling.framework.Event
import edu.pitt.cs3720.scheduling.framework.EventListener


abstract class Scheduler(private val jobs: MutableList<Job>, private val timeoutMillis: Int): EventListener {
    // Who's working on what. Also serves as a device registry
    private val schedule = mutableMapOf<Device, Job?>()


    override fun onEvent(event: Event) {
        event.deviceOnline()?.let { deviceOnline ->
            if (schedule.containsKey(deviceOnline.device)) {
                // The device was already registered; it failed and recovered before we could notice
                schedule.remove(deviceOnline.device)?.let { job ->
                    // If it had a job assigned, throw it back to the pool
                    jobs.add(job)
                }
            }
            scheduleWorkOn(deviceOnline.device, event.time)
        }
        event.deviceOffline()?.let { deviceOffline ->
            // Remove a device from the registry and return its job to the lake
            schedule.remove(deviceOffline.device)?.let { job ->
                jobs.add(job)
            }
        }
        event.workCompleted()?.let { workCompleted ->
            scheduleWorkOn(workCompleted.device, event.time)
        }
        event.timeout()?.let { timeout ->
            /**Controller.addEvent(Event(
                time = event.time + 50,
                payload = StatusRequest(),
                listener = timeout.device
            ))*/
        }
    }

    private fun scheduleWorkOn(device: Device, currentTime: Int) {
        // Schedule the work
        val nextJob = nextJobFor(device)
        schedule[device] = nextJob
        Controller.addEvent(Event(
            time = currentTime + 50,
            payload = WorkRequest(nextJob),
            listener = device
        ))

        // And set up a timeout
        Controller.addEvent(Event(
            time = currentTime + timeoutMillis,
            payload = Timeout(device = device, job = nextJob),
            listener = this
        ))
    }

    abstract fun nextJobFor(device: Device): Job
}
