package edu.pitt.cs3720.scheduling


data class Job(val size: Int, val dependencies: List<Job> = emptyList())
