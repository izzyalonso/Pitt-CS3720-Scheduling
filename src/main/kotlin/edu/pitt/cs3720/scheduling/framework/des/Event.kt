package edu.pitt.cs3720.scheduling.framework.des


data class Event(val time: Long, val payload: Payload, val listener: EventListener): Comparable<Event> {
    internal val id: Int

    init{
        id = count++
        events.add(this)
    }

    override fun equals(other: Any?) = if (other is Event) id == other else false
    override fun hashCode() = id
    override fun compareTo(other: Event): Int = time.compareTo(other.time)
    override fun toString() = "Event(time=$time, payload=$payload)"


    companion object {
        private var count = 0
        internal val events = mutableListOf<Event>()

        internal fun reset() {
            count = 0
            events.clear()
        }
    }
}

interface Payload

interface EventListener {
    fun onEvent(payload: Payload)
}
