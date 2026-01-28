package com.studysmart.features.practice.domain

class StartSessionUseCase(
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(quizId: Long): PracticeSession {
        // 1. Create new session in DB
        val sessionId = sessionRepository.createSession(quizId)
        
        // 2. Fetch session details (should be empty/idle)
        val initialSession = sessionRepository.getSession(sessionId) 
            ?: throw IllegalStateException("Failed to create session")
            
        // 3. Determine first question
        // Logic: Get all questions, pick first.
        val questions = sessionRepository.getQuestionsForQuiz(quizId)
        if (questions.isEmpty()) return initialSession.copy(state = SessionState.COMPLETED)
        
        // Simple strategy: Start with the first available question
        val firstQuestion = questions.first() // Randomize later if needed
        
        // 4. Return Session with IDLE -> QUESTION state
        return initialSession.copy(
            state = SessionState.QUESTION,
            currentQuestion = firstQuestion
        )
    }
}
