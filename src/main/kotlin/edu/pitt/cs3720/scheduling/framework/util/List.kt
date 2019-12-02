package edu.pitt.cs3720.scheduling.framework.util


/*
 * Set of extensions for List and List subclasses
 */


/**
 * Removes and returns an element from a list, or null if it wasn't found.
 */
fun <T> MutableList<T>.removeAndGet(element: T): T? {
    return when(val index = indexOf(element)){
        -1 -> null
        else -> removeAt(index)
    }
}

/**
 * Adds an [element] to [this] list and returns the list so that items can be added in a builder-like manner.
 */
fun <T> MutableList<T>.addAndReturnList(element: T): MutableList<T> {
    add(element)
    return this
}

/**
 * Creates a shallow copy of [this] list.
 */
fun <T> List<T>.duplicate(): List<T> = fold(mutableListOf()) { acc, next -> acc.addAndReturnList(next) }
