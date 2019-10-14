package edu.pitt.cs3720.scheduling.framework.des


data class Event(val time: Long, val payload: Payload, val listener: EventListener): Comparable<Event> {
    val id: Int

    init{
        id = ++count
    }
    override fun compareTo(other: Event): Int = time.compareTo(other.time)

    override fun toString() = "Event(time=$time, payload=$payload)"

    companion object {
        private var count = 0
    }
}

abstract class Payload {
    override fun toString() = this::class.simpleName ?: "Payload"
}

interface EventListener {
    fun onEvent(payload: Payload)
}
