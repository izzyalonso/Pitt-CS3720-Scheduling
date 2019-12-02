package edu.pitt.cs3720.scheduling.framework.util


/**
 * A circular buffer with a given [capacity]. Once the capacity is reached, the oldest item will be kicked out of
 * the buffer. Only allows integers.
 *
 * @author Ismael Alonso
 */
class CircularBuffer(val capacity: Int) {
    private val elements = mutableListOf<Int>()
    private var head = 0


    /**
     * Adds an [element] to the buffer.
     */
    fun add(element: Int) {
        if (elements.size < capacity) {
            elements.add(element)
            head++
        } else {
            elements[head++] = element
        }
        if (head == capacity) head = 0
    }

    /**
     * @return a sorted list containing all elements in the buffer
     */
    fun sorted() = elements.sorted()
}
