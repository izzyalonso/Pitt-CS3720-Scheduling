package edu.pitt.cs3720.scheduling.framework


data class Event(val time: Int, val payload: Payload, val listener: EventListener): Comparable<Event> {
    val id: Int

    init{
        id = ++count
    }
    override fun compareTo(other: Event): Int = time.compareTo(other.time)

    companion object {
        private var count = 0
    }
}

interface Payload

interface EventListener {
    fun onEvent(event: Event)
}
