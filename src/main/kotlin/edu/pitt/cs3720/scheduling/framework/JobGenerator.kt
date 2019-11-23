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
abstract class JobGenerator(protected val scheduler: Scheduler): EventListener {

    private var started = false


    /**
     * Starts adding jobs to the pool.
     */
    internal fun start() {
        if (started) return
        started = true
        // Let the job generation be the first thing to execute
        Controller.registerEvent(0, GenerateJob(), this)
    }

    /**
     * Adds a number of jobs with random sizes and frequencies in pre-established ranges.
     *
     * @param scheduler a reference to the scheduler to generate events.
     * @param sizes the job size range to draw from.
     * @param frequencies the job frequency range to draw from.
     * @param jobCap the number of jobs to generate.
     */
    class Random(
        scheduler: Scheduler,
        private val sizes: Range,
        private val frequencies: Range,
        private val jobCap: Int = 20
    ): JobGenerator(scheduler) {

        init {
            if (jobCap < 1) {
                throw IllegalArgumentException("At least one job per simulation, please.")
            }
        }

        private var generatedJobs = 0

        override fun onEvent(payload: Payload) {
            payload.generateJob()?.let { _ ->
                scheduler.addJob(Job(sizes.random()))
                if (++generatedJobs < jobCap) {
                    Controller.registerEvent(frequencies.random().toLong(), GenerateJob(), this)
                }
            }
        }
    }

    /**
     * This generator knows the set of jobs it needs to generate beforehand.
     *
     * @param scheduler the scheduler to release jobs to
     * @param jobs the list of jobs to release with a release time
     */
    class Fixed(scheduler: Scheduler, jobs: List<Pair<Job, Long>>): JobGenerator(scheduler) {
        private val jobs: MutableList<Pair<Job, Long>>

        init {
            // Sort the jobs by release time
            // Just a precaution
            this.jobs = jobs.toMutableList().sortedBy { job -> job.second } as MutableList<Pair<Job, Long>>
        }

        override fun onEvent(payload: Payload) {
            payload.generateJob()?.let { _ ->
                val nextTime = jobs.first().second
                // Release all the jobs with the same next time
                while (jobs.isNotEmpty() && jobs.first().second == nextTime) {
                    scheduler.addJob(jobs.removeAt(0).first)
                }

                // If there are still jobs to release, schedule te next release
                if (jobs.isNotEmpty()) {
                    Controller.registerEvent(
                        Controller.currentTimeMillis() - jobs.first().second,
                        GenerateJob(),
                        this
                    )
                }
            }
        }
    }
}
