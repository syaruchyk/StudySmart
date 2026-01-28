package com.studysmart.features.practice.domain

class GetSessionSummaryUseCase(
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(sessionId: Long): PracticeSession {
        return sessionRepository.getSession(sessionId) 
            ?: throw IllegalArgumentException("Session not found")
    }
}
