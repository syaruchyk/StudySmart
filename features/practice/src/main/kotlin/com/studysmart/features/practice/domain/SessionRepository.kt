package com.studysmart.features.practice.domain

import com.studysmart.core.data.db.entities.AttemptEntity

interface SessionRepository {
    suspend fun createSession(quizId: Long): Long
    suspend fun getSession(sessionId: Long): PracticeSession?
    
    // Get a specific question or the next recommended one
    suspend fun getQuestion(questionId: Long): SessionQuestion?
    suspend fun getQuestionsForQuiz(quizId: Long): List<SessionQuestion>
    
    suspend fun recordAttempt(
        sessionId: Long,
        questionId: Long, 
        optionId: Long?, 
        isCorrect: Boolean, 
        timeTakenMs: Long
    )
    
    suspend fun getSrsData(questionId: Long): SrsData?
    
    suspend fun getAttempts(sessionId: Long): List<AttemptEntity>

    suspend fun updateSrsState(questionId: Long, srsData: SrsData)
    
    suspend fun getRecentFailures(limit: Int): List<Long> // Returns list of questionIds
}
