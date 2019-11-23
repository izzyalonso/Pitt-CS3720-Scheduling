package edu.pitt.cs3720.scheduling.framework.des


/**
 * Controls the discrete event simulation. A singleton.
 *
 * @author Ismael Alonso
 */
object Controller {
    private var running = false

    private var eventQueue = EventQueue()
    private var currentTimeMillis = 0L


    /**
     * Resets the controller.
     */
    fun reset() {
        running = false
        eventQueue = EventQueue()
        Event.reset()
        currentTimeMillis = 0
    }

    /**
     * @return the current simulation time.
     */
    fun currentTimeMillis() = currentTimeMillis

    /**
     * Registers a full event. Typically for set up. Skips registration if the event happened in the past.
     *
     * @param event the event to register.
     */
    fun registerEvent(event: Event){
        if (event.time < currentTimeMillis) return
        eventQueue.enqueue(event)
    }

    /**
     * Registers an event with a fixed delay. TODO make it configurable.
     */
    fun registerEvent(payload: Payload, listener: EventListener): Int {
        return registerEvent(50, payload, listener)
    }

    /**
     * Registers an event some time in the future.
     */
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

    /**
     * Executes the simulation.
     */
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
