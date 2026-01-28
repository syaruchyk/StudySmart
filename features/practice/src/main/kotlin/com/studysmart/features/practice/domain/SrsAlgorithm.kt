package com.studysmart.features.practice.domain

import kotlin.math.max
import kotlin.math.roundToInt

object SrsAlgorithm {

    /**
     * Calculates the next SRS state using a simplified SM-2 algorithm.
     *
     * @param current The current SRS state of the item (or null if new).
     * @param isCorrect Whether the user answered correctly.
     * @param now Current timestamp in milliseconds.
     * @return The updated SRS state.
     */
    fun calculateNext(current: SrsData?, isCorrect: Boolean, now: Long = System.currentTimeMillis()): SrsData {
        val state = current ?: SrsData.INITIAL

        // Quality of response (q):
        // 5 - perfect response (we map 'correct' to 5 or 4 usually)
        // 3 - correct response but with difficulty
        // ...
        // 0 - complete blackout
        // For simplicity with boolean input:
        // Correct -> 5 (Easy/Standard) - could be refined with response time if available
        // Incorrect -> 0
        val quality = if (isCorrect) 5 else 0

        var newEase = state.easeFactor
        var newInterval: Int
        var newRepetitions = state.repetitions

        if (quality >= 3) {
            // Correct answer
            // Update Ease Factor: EF' = EF + (0.1 - (5 - q) * (0.08 + (5 - q) * 0.02))
            // Since q=5 for correct: EF' = EF + 0.1
            newEase = state.easeFactor + 0.1
            
            // Increment repetitions
            newRepetitions += 1

            // Calculate Interval
            if (newRepetitions == 1) {
                newInterval = 1
            } else if (newRepetitions == 2) {
                newInterval = 6
            } else {
                newInterval = (state.intervalDays * newEase).roundToInt()
            }
        } else {
            // Incorrect answer
            newRepetitions = 0
            newInterval = 1 // Reset to 1 day (or could be 0 for immediate review)
            // Ease factor optionally decreases, though standard SM-2 doesn't drop it drastically on failure, 
            // but often resets repetitions. Let's keep ease same or slightly lower to be conservative.
            newEase = max(1.3, state.easeFactor - 0.2)
        }

        // Cap Ease Factor
        if (newEase < 1.3) newEase = 1.3

        // Calculate Due Date
        val oneDayMs = 24 * 60 * 60 * 1000L
        val nextDue = now + (newInterval * oneDayMs)

        return SrsData(
            easeFactor = newEase,
            intervalDays = newInterval,
            repetitions = newRepetitions,
            dueAt = nextDue
        )
    }
}
