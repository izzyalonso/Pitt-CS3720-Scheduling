package edu.pitt.cs3720.scheduling.framework.des

import edu.pitt.cs3720.scheduling.framework.Simulation


object Controller {
    private var running = false

    private var eventQueue = EventQueue()
    private var currentTimeMillis = 0L


    private fun reset() {
        running = false
        eventQueue = EventQueue()
        Event.reset()
        currentTimeMillis = 0
    }

    fun registerEvent(payload: Payload, listener: EventListener): Int {
        return registerEvent(50, payload, listener)
    }

    fun registerEvent(millisFromNow: Long, payload: Payload, listener: EventListener): Int {
        val event = Event(currentTimeMillis + millisFromNow, payload, listener)
        eventQueue.enqueue(event)
        return event.id
    }

    /**
     * Removes an event form the queue.
     *
     * @param eventHandle the handle of an event as returned by Controller#registerEvent.
     */
    fun removeEvent(eventHandle: Int) {
        eventQueue.remove(Event.events[eventHandle])
    }

    fun run(simulation: Simulation) {
        if (running) return

        reset()
        for (event in simulation.setupEvents()) {
            eventQueue.enqueue(event)
        }
        running = true
        while (!eventQueue.isEmpty()) {
            val event = eventQueue.dequeue()
            println("$event")
            currentTimeMillis = event.time
            event.listener.onEvent(event.payload)
        }
        running = false
    }
}
