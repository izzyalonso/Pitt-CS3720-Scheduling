package edu.pitt.cs3720.scheduling

import edu.pitt.cs3720.scheduling.framework.*
import edu.pitt.cs3720.scheduling.framework.des.Event
import java.util.concurrent.TimeUnit


/*
 * SIMPLE.
 *
 * Contains a simple scheduler and a simple simulation.
 *
 * Primary for testing purposes and to establish some sort of performance baseline.
 *
 * The simple scheduler is a FIFO matching scheduler. Schedules the first available job on the first available device.
 */

class SimpleScheduler: Scheduler(TimeUnit.SECONDS.toMillis(5)) {
    override fun scheduleWork() {
        schedule(jobs.first(), idleDevices.first())
    }
}

class SimpleSimulation: Simulation() {
    private val scheduler = SimpleScheduler()
    private val devices: List<Device>
    private val events: List<Event>

    init {
        devices = listOf(
            Device(scheduler, 1000, 1.1f, 0f, "Phone 1"),
            Device(scheduler, 500, 1f, 0f, "Phone 2"),
            Device(scheduler, 2000, 1.05f, 0.05f, "Phone 3"),
            Device(scheduler, 4000, 1.3f, 0f, "Phone 4"),
            Device(scheduler, 2000, 1.15f, 0.15f, "Phone 5")
        )

        events = listOf(
            awakeEvent(50, devices[0]),
            sleepEvent(TimeUnit.SECONDS.toMillis(10), devices[0]),
            awakeEvent(TimeUnit.SECONDS.toMillis(15), devices[0]),
            awakeEvent(100, devices[1]),
            awakeEvent(150, devices[2]),
            awakeEvent(200, devices[3]),
            awakeEvent(250, devices[4])
        )
    }

    override fun scheduler() = scheduler

    override fun devices() = devices

    override fun setupEvents() = events
}
