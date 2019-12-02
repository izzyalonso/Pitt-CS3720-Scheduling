package edu.pitt.cs3720.scheduling.framework


object Analytics {
    var totalJobs = 0
        private set

    var jobsCompleted = 0
        private set

    var jobSizeCompleted = 0
        private set

    var missedDeadlines = 0
        private set

    var missedSize = 0
        private set


    fun reset() {
        totalJobs = 0
        jobsCompleted = 0
        jobSizeCompleted = 0
        missedDeadlines = 0
        missedSize = 0
    }

    fun jobCompleted(job: Job, device: Device) {
        totalJobs++
        jobsCompleted++
        jobSizeCompleted += job.size
    }

    fun jobMissedDeadline(job: Job) {
        totalJobs++
        missedDeadlines++
        missedSize += job.size
    }
}