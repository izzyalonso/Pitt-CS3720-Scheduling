package edu.pitt.cs3720.scheduling.framework.util


/**
 * This is a kind of crap implementation of a sorted list, but this is unimportant, this system is not built for
 * performance. Sorts all elements with the provided [comparator].
 *
 * @author Ismael Alonso
 */
class SortedArrayList<T>(private val comparator: Comparator<T>): ArrayList<T>(){

    /**
     * Adds an [element] to the list.
     */
    override fun add(element: T): Boolean {
        val success = super.add(element)
        sortWith(comparator)
        return success
    }

    /**
     * @throws UnsupportedOperationException the [comparator] owns the indexing decision.
     */
    override fun add(index: Int, element: T) {
        throw UnsupportedOperationException()
    }

    /**
     * Adds all [elements] to the list.
     */
    override fun addAll(elements: Collection<T>): Boolean {
        val success = super.addAll(elements)
        sortWith(comparator)
        return success
    }

    /**
     * @throws UnsupportedOperationException the [comparator] owns the indexing decision.
     */
    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        throw UnsupportedOperationException()
    }
}
