package edu.pitt.cs3720.scheduling

import edu.pitt.cs3720.scheduling.framework.Device
import edu.pitt.cs3720.scheduling.framework.Job
import edu.pitt.cs3720.scheduling.framework.Scheduler
import java.util.concurrent.TimeUnit


class SimpleScheduler: Scheduler(TimeUnit.SECONDS.toMillis(5)) {
    override fun addJobToPool(job: Job) {

    }

    override fun nextJobFor(device: Device): Job {
        return jobs.removeAt(0)
    }
}
