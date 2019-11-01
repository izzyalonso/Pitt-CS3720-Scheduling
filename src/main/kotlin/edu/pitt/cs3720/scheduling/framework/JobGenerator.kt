package edu.pitt.cs3720.scheduling.framework

import edu.pitt.cs3720.scheduling.framework.des.Controller
import edu.pitt.cs3720.scheduling.framework.des.EventListener
import edu.pitt.cs3720.scheduling.framework.des.Payload
import edu.pitt.cs3720.scheduling.framework.util.Range
import java.lang.IllegalArgumentException


/**
 * Adds a finite amount of jobs to the scheduler with a size range at a frequency range. This is done online
 * to keep things real.
 */
class JobGenerator(
    private val scheduler: Scheduler,
    private val sizes: Range,
    private val frequencies: Range,
    private val jobCap: Int = 20
): EventListener {

    init {
        if (jobCap < 1) {
            throw IllegalArgumentException("At least one job per simulation, please.")
        }
    }

    private var started = false
    private var generatedJobs = 0


    /**
     * Starts adding jobs to the pool.
     */
    internal fun start() {
        if (started) return
        started = true
        // Let the job generation be the first thing to execute
        Controller.registerEvent(0, GenerateJob(), this)
    }

    override fun onEvent(payload: Payload) {
        payload.generateJob()?.let { _ ->
            scheduler.addJob(Job(sizes.random()))
            if (++generatedJobs < jobCap) {
                Controller.registerEvent(frequencies.random().toLong(), GenerateJob(), this)
            }
        }
    }
}
