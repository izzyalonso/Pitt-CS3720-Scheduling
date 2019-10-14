package edu.pitt.cs3720.scheduling

import edu.pitt.cs3720.scheduling.framework.*
import edu.pitt.cs3720.scheduling.framework.des.Event
import java.util.concurrent.TimeUnit

class SimpleSimulation: Simulation {

    private val scheduler = scheduler()
    private val devices: List<Device>
    private val events: List<Event>

    init {
        devices = listOf(
            Device(scheduler, 1000, 0f),
            Device(scheduler, 500, 0f),
            Device(scheduler, 2000, 0f)
        )

        events = listOf(
            Event(0, Awake(), devices[0]),
            Event(TimeUnit.SECONDS.toMillis(10), Sleep(), devices[0]),
            Event(0, Awake(), devices[1]),
            Event(0, Awake(), devices[2])
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
