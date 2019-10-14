package edu.pitt.cs3720.scheduling.framework

import edu.pitt.cs3720.scheduling.framework.des.Event


interface Simulation {
    /**
     * The scheduler.
     */
    fun scheduler(): Scheduler

    /**
     * The list of jobs to run.
     */
    fun jobs(): List<Job>

    /**
     * The list of devices in the IoT.
     */
    fun devices(): List<Device>

    /**
     * Events registered before the simulation starts.
     *
     * When devices are programmed to boot up or shut down.
     */
    fun setupEvents(): List<Event>
}
