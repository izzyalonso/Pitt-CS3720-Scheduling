package edu.pitt.cs3720.scheduling.framework.des

import java.util.*


/**
 * A priority queue for events. Events are sorted in ascending time order.
 */
internal class EventQueue {
    private val queue = PriorityQueue<Event>()

    /**
     * Enqueues an event.
     *
     * @param event the event to enqueue.
     */
    internal fun enqueue(event: Event) = queue.add(event)

    /**
     * @return true if the queue is empty, false otherwise.
     */
    internal fun isEmpty() = queue.isEmpty()

    /**
     * @return the next event in line.
     */
    internal fun dequeue(): Event = queue.remove()

    /**
     * Removes an event from the queue altogether. Take the following scenario:
     *
     * Suppose we schedule a timeout type event but we get a legit response before we actually hit
     * the timeout. It is then in our interest to remove the event from the queue to get a cleaner
     * output, as the implementer is likely to ignore the event anywho.
     */
    internal fun remove(event: Event) = queue.remove(event)
}
