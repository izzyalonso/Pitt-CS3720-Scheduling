package edu.pitt.cs3720.scheduling

import edu.pitt.cs3720.scheduling.framework.Scheduler
import java.util.concurrent.TimeUnit


class SimpleScheduler: Scheduler(TimeUnit.SECONDS.toMillis(5)) {
    override fun scheduleWork() {
        schedule(jobs.first(), idleDevices.first())
    }
}
