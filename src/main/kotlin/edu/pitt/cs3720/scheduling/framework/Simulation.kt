package edu.pitt.cs3720.scheduling.framework

import edu.pitt.cs3720.scheduling.framework.des.Controller
import edu.pitt.cs3720.scheduling.framework.des.Event
import edu.pitt.cs3720.scheduling.framework.util.Range


abstract class Simulation {
    /**
     * The scheduler.
     */
    abstract fun scheduler(): Scheduler

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
     * @return the default job generator, parameters may change in the future.
     */
    fun jobGenerator() = JobGenerator.Random(
        scheduler = scheduler(),
        sizes = Range(500, 8000),
        deadlines = Range(5000, 8000),
        frequencies = Range(500, 2000)
    )

    /**
     * Runs the simulation.
     */
    fun run() {
        Controller.reset()
        jobGenerator().start()
        for (event in setupEvents()) {
            Controller.registerEvent(event)
        }
        Controller.run(object: Controller.Listener {
            override fun onSimulationComplete() {
                println("SIMULATION COMPLETE")
                println("-------------------")
                println("Jobs submitted: ${Analytics.totalJobs}" )
                println("Jobs completed: ${Analytics.jobsCompleted}")
                println("Job size completed: ${Analytics.jobSizeCompleted}")
                println("Deadlines missed: ${Analytics.missedDeadlines}")
                println("Size lost due to deadline misses: ${Analytics.missedSize}")
            }
        })
    }
}
