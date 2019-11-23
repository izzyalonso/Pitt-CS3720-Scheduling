package edu.pitt.cs3720.scheduling.framework.util


/**
 * Removes and returns an element from a list, or null if it wasn't found.
 */
fun <T> MutableList<T>.removeAndGet(element: T): T? {
    return when(val index = indexOf(element)){
        -1 -> null
        else -> removeAt(index)
    }
}
