package edu.pitt.cs3720.scheduling.framework.des

import java.util.*


internal class EventQueue {
    private val queue = PriorityQueue<Event>()

    internal fun enqueue(event: Event) = queue.add(event)
    internal fun isEmpty() = queue.isEmpty()
    internal fun dequeue(): Event = queue.remove()

    /**
     * There are instances in which we schedule a timeout type, but we get a legit response before
     * we hit the timeout. It is then in our interest to remove the event from the queue to get
     * a cleaner output.
     */
    internal fun remove(event: Event) = queue.remove(event)
}
