package edu.pitt.cs3720.scheduling.framework


object Controller {
    var eventQueue = EventQueue()
    var currentTimeMillis = 0


    fun reset() {
        eventQueue = EventQueue()
        currentTimeMillis = 0
    }

    fun addEvent(event: Event){
        if (event.time < currentTimeMillis) {
            return
        }
        eventQueue.enqueue(event)
    }

    fun runLoop() {
        while (!eventQueue.isEmpty()) {
            val event = eventQueue.dequeue()
            currentTimeMillis = event.time
            event.listener.onEvent(event)
        }
    }
}
