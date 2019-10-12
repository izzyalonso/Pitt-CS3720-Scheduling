package edu.pitt.cs3720.scheduling

import edu.pitt.cs3720.scheduling.framework.Event
import edu.pitt.cs3720.scheduling.framework.EventListener


abstract class Scheduler: EventListener {
    val devices = mutableListOf<Device>()


    override fun onEvent(event: Event) {
        event.payload.deviceOnline()?.let { deviceOnline ->
            devices.add(deviceOnline.device)
        }
        event.payload.deviceOffline()?.let { deviceOffline ->
            devices.remove(deviceOffline.device)
        }
    }
}
