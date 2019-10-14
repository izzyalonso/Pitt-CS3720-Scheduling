package edu.pitt.cs3720.scheduling.framework

import edu.pitt.cs3720.scheduling.framework.des.Controller
import edu.pitt.cs3720.scheduling.framework.des.Event


abstract class Simulation {
    /**
     * The scheduler.
     */
    abstract fun scheduler(): Scheduler

    /**
     * The list of jobs to run.
     */
    abstract fun jobs(): List<Job>

    /**
     * The list of devices in the IoT.
     */
    abstract fun devices(): List<Device>

    /**
     * Events registered before the simulation starts.
     *
     * When devices are programmed to boot up or shut down.
     */
    abstract fun setupEvents(): List<Event>

    /**
     * Runs the simulation.
     */
    fun run() {
        Controller.reset()
        for (event in setupEvents()) {
            Controller.registerEvent(event)
        }
        Controller.run()
    }
}
