package edu.pitt.cs3720.scheduling.framework.des


/**
 * A queuable event.
 *
 * @param time the time at which the event will happen.
 * @param payload the payload of the event.
 * @param listener the listener of this event.
 * @see Payload
 *
 * @author Ismael Alonso
 */
class Event(val time: Long, val payload: Payload, val listener: EventListener): Comparable<Event> {
    internal val id: Int

    init{
        id = count++
        events.add(this)
    }


    /*
     * Any implementations:
     *
     * equals to remove from the queue.
     * hashCode because of equals.
     * compareTo for the priority queue.
     * toString because it is instrumental to log this events neatly.
     */
    override fun equals(other: Any?) = if (other is Event) id == other.id else false
    override fun hashCode() = id
    override fun compareTo(other: Event): Int = time.compareTo(other.time)
    override fun toString() = "Event(time=$time, payload=$payload)"


    /*
     * Keeps a list of all events generated in this run, indexed by id.
     */
    companion object {
        private var count = 0
        internal val events = mutableListOf<Event>()

        internal fun reset() {
            count = 0
            events.clear()
        }
    }
}

/**
 * An event's payload. Implement this interface for custom typing.
 *
 * @author Ismael Alonso
 */
interface Payload

/**
 * Adds the ability to listen to events.
 *
 * @author Ismael Alonso
 */
interface EventListener {
    /**
     * Called when an event the implementer is registered to triggers.
     *
     * @param payload the payload of the event.
     */
    fun onEvent(payload: Payload)
}
