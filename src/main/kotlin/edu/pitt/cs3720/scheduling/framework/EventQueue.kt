package edu.pitt.cs3720.scheduling.framework

import java.util.*


class EventQueue {
    private val queue = PriorityQueue<Event>()

    fun enqueue(event: Event) = queue.add(event)
    fun isEmpty() = queue.isEmpty()
    fun dequeue(): Event = queue.remove()
}
