package edu.pitt.cs3720.scheduling.framework.util


class CircularBuffer(val capacity: Int) {
    private val elements = mutableListOf<Int>()
    private var head = 0


    fun add(element: Int) {
        if (elements.size < capacity) {
            elements.add(element)
            head++
        } else {
            elements[head++] = element
        }
        if (head == capacity) head = 0
    }

    fun sorted() = elements.sorted()
}
