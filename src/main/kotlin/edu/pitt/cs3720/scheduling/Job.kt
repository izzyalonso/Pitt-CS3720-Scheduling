package edu.pitt.cs3720.scheduling


data class Job(val id: Int, val size: Int, val dependencies: List<Job> = emptyList()) {
    override fun equals(other: Any?): Boolean {
        if (other is Job) {
            return id == other.id
        }
        return false
    }

    override fun hashCode() = id
}
