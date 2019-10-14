package edu.pitt.cs3720.scheduling.framework


data class Job(val size: Int, val dependencies: List<Job> = emptyList()) {
    val id: Int

    init {
        id = ++jobCount
    }

    override fun equals(other: Any?): Boolean {
        if (other is Job) {
            return id == other.id
        }
        return false
    }

    override fun hashCode() = id
    override fun toString() = "#$id<$size>"


    companion object {
        private var jobCount = 0
    }
}
