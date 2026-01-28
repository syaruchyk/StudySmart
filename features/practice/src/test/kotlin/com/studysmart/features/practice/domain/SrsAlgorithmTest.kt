package com.studysmart.features.practice.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SrsAlgorithmTest {

    @Test
    fun `initial calculation should return standard first interval`() {
        val next = SrsAlgorithm.calculateNext(null, isCorrect = true)
        
        assertEquals(1, next.repetitions)
        assertEquals(1, next.intervalDays)
        assertTrue(next.easeFactor > 2.5) // 2.5 + 0.1
    }

    @Test
    fun `correct answer increases interval and ease`() {
        val current = SrsData(
            easeFactor = 2.5,
            intervalDays = 1,
            repetitions = 1,
            dueAt = 1000L
        )
        
        val next = SrsAlgorithm.calculateNext(current, isCorrect = true)
        
        assertEquals(2, next.repetitions)
        assertEquals(6, next.intervalDays) // 2nd rep is 6 days
        assertEquals(2.6, next.easeFactor, 0.01)
    }

    @Test
    fun `incorrect answer resets repetitions and interval`() {
        val current = SrsData(
            easeFactor = 2.8,
            intervalDays = 10,
            repetitions = 5,
            dueAt = 1000L
        )
        
        val next = SrsAlgorithm.calculateNext(current, isCorrect = false)
        
        assertEquals(0, next.repetitions)
        assertEquals(1, next.intervalDays)
        assertTrue(next.easeFactor < 2.8) // Ease drops
    }
}
