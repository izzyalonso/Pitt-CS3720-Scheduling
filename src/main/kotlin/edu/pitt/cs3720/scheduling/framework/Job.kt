package edu.pitt.cs3720.scheduling.framework


/**
 * A job that can be scheduled.
 *
 * @param size a representation of the size of the job.
 * @param dependencies jobs that need to be run before this one.
 */
data class Job(val size: Int, val dependencies: List<Job> = emptyList()) {
    val id: Int

    init {
        id = ++jobCount
    }

    override fun equals(other: Any?) = other is Job && id == other.id
    override fun hashCode() = id
    override fun toString() = "#$id<$size>"


    companion object {
        private var jobCount = 0
    }
}
