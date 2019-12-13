package edu.pitt.cs3720.scheduling

import edu.pitt.cs3720.scheduling.framework.Job

object JobRuns {
    fun offlineSetupRun() = listOf(
        Pair(Job(size = 10000, deadline = 10000), 500L),
        Pair(Job(size = 4000, deadline = 10000), 500L),
        Pair(Job(size = 5000, deadline = 10000), 500L),
        Pair(Job(size = 3000, deadline = 10000), 500L),
        Pair(Job(size = 2000, deadline = 10000), 500L),
        Pair(Job(size = 4000, deadline = 2000), 600L)
    )
}
