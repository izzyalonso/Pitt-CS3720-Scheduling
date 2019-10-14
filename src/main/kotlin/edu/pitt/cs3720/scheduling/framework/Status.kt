package edu.pitt.cs3720.scheduling.framework


data class Status(val working: Boolean) {
    override fun toString() = if (working) "working" else "idle"
}
