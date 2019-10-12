package edu.pitt.cs3720.scheduling

import edu.pitt.cs3720.scheduling.framework.Event
import edu.pitt.cs3720.scheduling.framework.EventListener


data class Device(val id: Int, val power: Int, val failureRate: Float) {
    override fun equals(other: Any?): Boolean {
        if (other is Device) {
            return id == other.id
        }
        return false
    }

    override fun hashCode() = id
}

class DeviceController(val device: Device): EventListener {
    override fun onEvent(event: Event) {
        event.payload.workCompleted()?.let { workRequest ->
            // Schedule the next event... or don't
        }
    }
}
