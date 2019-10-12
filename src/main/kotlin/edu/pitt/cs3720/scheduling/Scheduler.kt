package edu.pitt.cs3720.scheduling

import edu.pitt.cs3720.scheduling.framework.Event
import edu.pitt.cs3720.scheduling.framework.EventListener


abstract class Scheduler(private val jobs: MutableList<Job>, private val timeoutMillis: Int): EventListener {
    // Who's working on what. Also serves as a device registry
    private val schedule = mutableMapOf<Device, Job?>()


    override fun onEvent(event: Event) {
        event.deviceOnline()?.let { deviceOnline ->
            schedule.put(deviceOnline.device, null)
            // TODO schedule work on this device
        }
        event.deviceOffline()?.let { deviceOffline ->
            // Remove a device from the registry and return its job to the lake
            schedule.remove(deviceOffline.device)?.let { job ->
                jobs.add(job)
            }
        }
        event.timeout()?.let { timeout ->

        }
    }
}
