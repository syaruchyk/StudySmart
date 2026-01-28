package com.studysmart.features.practice.domain

/**
 * Represents the current state of a practice session.
 */
data class PracticeSession(
    val id: Long,
    val quizId: Long,
    val state: SessionState = SessionState.IDLE,
    val currentQuestion: SessionQuestion? = null,
    val stats: SessionStats = SessionStats(),
    val completedAt: Long? = null
)

data class SessionStats(
    val totalQuestionsAnswered: Int = 0,
    val correctAnswers: Int = 0,
    val startTime: Long = System.currentTimeMillis()
)

/**
 * A question presented during a session, decoupled from database entities.
 */
data class SessionQuestion(
    val id: Long,
    val text: String,
    val options: List<SessionOption>,
    val explanation: String?,
    val srsData: SrsData? = null
)

data class SessionOption(
    val id: Long,
    val text: String,
    val isCorrect: Boolean
)

/**
 * Data required for the Spaced Repetition Algorithm (SM-2).
 */
data class SrsData(
    val easeFactor: Double,
    val intervalDays: Int,
    val repetitions: Int,
    val dueAt: Long
) {
    companion object {
        val INITIAL = SrsData(
            easeFactor = 2.5,
            intervalDays = 0,
            repetitions = 0,
            dueAt = 0L
        )
    }
}

/**
 * State machine for the UI/Interaction flow.
 */
enum class SessionState {
    IDLE,       // Initial state
    LOADING,    // Loading next question
    QUESTION,   // Displaying question, waiting for input
    ANSWERED,   // User answered, showing immediate result (correct/incorrect)
    FEEDBACK,   // Showing detailed explanation (can be skipped to NEXT)
    COMPLETED   // Session finished
}
