package edu.pitt.cs3720.scheduling

import edu.pitt.cs3720.scheduling.framework.*
import edu.pitt.cs3720.scheduling.framework.des.Event
import java.util.concurrent.TimeUnit


class SimpleSimulation: Simulation() {
    private val scheduler = SimpleScheduler()
    private val devices: List<Device>
    private val events: List<Event>

    init {
        devices = listOf(
            Device(scheduler, 1000, 1f, 0f, "Phone 1"),
            Device(scheduler, 500, 1f, 0f, "Phone 2"),
            Device(scheduler, 2000, 1f, 0f, "Phone 3")
        )

        events = listOf(
            awakeEvent(50, devices[0]),
            sleepEvent(TimeUnit.SECONDS.toMillis(10), devices[0]),
            awakeEvent(100, devices[1]),
            awakeEvent(150, devices[2])
        )
    }

    override fun scheduler() = scheduler

    override fun devices() = devices

    override fun setupEvents() = events
}
