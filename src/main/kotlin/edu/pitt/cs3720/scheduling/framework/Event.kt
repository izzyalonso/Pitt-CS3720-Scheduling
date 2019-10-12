package edu.pitt.cs3720.scheduling.framework


data class Event(val time: Int, val payload: Payload, val listener: EventListener): Comparable<Event> {
    override fun compareTo(other: Event): Int = time.compareTo(other.time)
}

interface Payload

interface EventListener {
    fun onEvent(event: Event)
}
