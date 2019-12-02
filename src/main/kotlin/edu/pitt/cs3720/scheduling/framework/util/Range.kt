package edu.pitt.cs3720.scheduling.framework.util

import java.lang.IllegalArgumentException
import kotlin.random.Random


/**
 * Range of two integers with some added utility.
 *
 * @author Ismael Alonso
 */
class Range(private val from: Int, private val to: Int) {
    init {
        if (from > to) {
            throw IllegalArgumentException("From needs to be smaller than to in a range")
        }
    }


    /**
     * @return a random number in the range.
     */
    fun random() = from + Random.nextInt(to - from)
}
