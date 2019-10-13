package edu.pitt.cs3720.scheduling.framework

import java.util.*


internal class EventQueue {
    private val queue = PriorityQueue<Event>()

    internal fun enqueue(event: Event) = queue.add(event)
    internal fun isEmpty() = queue.isEmpty()
    internal fun dequeue(): Event = queue.remove()
}
