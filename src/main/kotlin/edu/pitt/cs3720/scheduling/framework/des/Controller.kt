package edu.pitt.cs3720.scheduling.framework.des


object Controller {
    private var running = false

    private var eventQueue = EventQueue()
    private var currentTimeMillis = 0L


    fun reset() {
        running = false
        eventQueue = EventQueue()
        Event.reset()
        currentTimeMillis = 0
    }

    fun registerEvent(event: Event) = eventQueue.enqueue(event)

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
        if (eventHandle < 0 || eventHandle >= Event.events.size) return
        eventQueue.remove(Event.events[eventHandle])
    }

    fun run() {
        if (running) return

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
