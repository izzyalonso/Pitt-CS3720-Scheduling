package edu.pitt.cs3720.scheduling

import edu.pitt.cs3720.scheduling.framework.*
import edu.pitt.cs3720.scheduling.framework.des.Event
import java.util.concurrent.TimeUnit


class SimpleSimulation: Simulation() {
    private val scheduler = scheduler()
    private val devices: List<Device>
    private val events: List<Event>

    init {
        devices = listOf(
            Device(scheduler, 1000, 0f, "Phone 1"),
            Device(scheduler, 500, 0f, "Phone 2"),
            Device(scheduler, 2000, 0f, "Phone 3")
        )

        events = listOf(
            awakeEvent(0, devices[0]),
            sleepEvent(TimeUnit.SECONDS.toMillis(10), devices[0]),
            awakeEvent(0, devices[1]),
            awakeEvent(0, devices[2])
        )
    }

    override fun scheduler(): Scheduler{
        val scheduler = SimpleScheduler()
        scheduler.jobs.addAll(jobs())
        return scheduler
    }

    override fun jobs() = listOf(
        Job(800),
        Job(5000),
        Job(8000),
        Job(2000),
        Job(6000),
        Job(15000),
        Job(8000),
        Job(9000)
    )

    override fun devices() = devices

    override fun setupEvents() = events
}
