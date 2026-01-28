package com.studysmart.features.practice.domain

class AnswerQuestionUseCase(
    private val sessionRepository: SessionRepository,
    private val updateSrsUseCase: UpdateSrsUseCase
) {
    suspend operator fun invoke(
        session: PracticeSession,
        optionId: Long,
        timeTakenMs: Long
    ): PracticeSession {
        val currentQuestion = session.currentQuestion 
            ?: throw IllegalStateException("No current question to answer")
            
        // 1. Verify Answer
        val selectedOption = currentQuestion.options.find { it.id == optionId }
        val isCorrect = selectedOption?.isCorrect == true
        
        // 2. Record Attempt
        sessionRepository.recordAttempt(
            sessionId = session.id,
            questionId = currentQuestion.id,
            optionId = optionId,
            isCorrect = isCorrect,
            timeTakenMs = timeTakenMs
        )
        
        // 3. Update SRS
        updateSrsUseCase(currentQuestion.id, isCorrect)
        
        // 4. Return Session in FEEDBACK/ANSWERED state
        // We update stats immediately for UI
        val newCorrectCount = session.stats.correctAnswers + (if (isCorrect) 1 else 0)
        val newTotal = session.stats.totalQuestionsAnswered + 1
        
        return session.copy(
            state = SessionState.ANSWERED, // Or FEEDBACK
            stats = session.stats.copy(
                totalQuestionsAnswered = newTotal,
                correctAnswers = newCorrectCount
            )
            // Note: currentQuestion remains same so UI can show feedback.
            // Client calls 'NextQuestionUseCase' (or similar) to proceed.
        )
    }
}
