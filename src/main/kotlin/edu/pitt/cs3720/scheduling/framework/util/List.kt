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

fun <T> MutableList<T>.addBuilder(element: T): MutableList<T> {
    add(element)
    return this
}

fun <T> List<T>.duplicate(): List<T> = fold(mutableListOf()) { acc: MutableList<T>, next: T -> acc.addBuilder(next) }
