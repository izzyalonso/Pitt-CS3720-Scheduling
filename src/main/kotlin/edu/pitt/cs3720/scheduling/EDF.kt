package edu.pitt.cs3720.scheduling

import edu.pitt.cs3720.scheduling.framework.*
import edu.pitt.cs3720.scheduling.framework.des.Controller
import edu.pitt.cs3720.scheduling.framework.des.Event
import edu.pitt.cs3720.scheduling.framework.util.SortedArrayList
import edu.pitt.cs3720.scheduling.framework.util.duplicate
import java.util.concurrent.TimeUnit


class EDFScheduler: Scheduler() {

    private val availableDevices = SortedArrayList<Device>(compareBy { it.capability })


    override fun deviceOnline(device: Device) {
        availableDevices.add(device)
    }

    override fun deviceOffline(device: Device, job: Job?) {
        availableDevices.remove(device)
    }

    override fun deviceLost(device: Device, job: Job?) {
        availableDevices.remove(device)
    }

    override fun scheduleWork() {
        val jobsDupe = jobs.duplicate().sortedBy { it.deadline }
        for (job in jobsDupe) {
            val requiredCapability = job.size / (job.deadline-Controller.currentTimeMillis()+200)
            for (device in availableDevices) {
                // Schedule jobs in devices that are just capable enough
                if (device.isIdle() && device.capability > requiredCapability) {
                    schedule(job, device)
                    break
                }
            }
        }
    }
}

class EDFSimulation: Simulation() {
    private val scheduler = EDFScheduler()
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
