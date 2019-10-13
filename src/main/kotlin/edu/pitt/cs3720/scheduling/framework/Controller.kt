package edu.pitt.cs3720.scheduling.framework


object Controller {
    private var eventQueue = EventQueue()
    private var currentTimeMillis = 0


    fun reset() {
        eventQueue = EventQueue()
        currentTimeMillis = 0
    }

    fun registerEvent(payload: Payload, listener: EventListener) {
        registerEvent(50, payload, listener)
    }

    fun registerEvent(millisFromNow: Int, payload: Payload, listener: EventListener) {
        eventQueue.enqueue(Event(currentTimeMillis+millisFromNow, payload, listener))
    }

    fun runLoop() {
        while (!eventQueue.isEmpty()) {
            val event = eventQueue.dequeue()
            currentTimeMillis = event.time
            event.listener.onEvent(event.payload)
        }
    }
}
