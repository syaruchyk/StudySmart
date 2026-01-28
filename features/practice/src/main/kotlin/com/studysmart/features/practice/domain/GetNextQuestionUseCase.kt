package com.studysmart.features.practice.domain

import com.studysmart.core.data.db.entities.AttemptEntity

class GetNextQuestionUseCase(
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(session: PracticeSession): PracticeSession {
        // 1. Fetch context
        val quizId = session.quizId
        val questions = sessionRepository.getQuestionsForQuiz(quizId)
        val attempts = sessionRepository.getAttempts(session.id) // Need to add getAttempts to Repo interface
        // FIX: I added getAttempts to DAO but not explicit to Repo interface in previous step? 
        // I added 'getSrsData' and 'recordAttempt'. I didn't add 'getAttempts'.
        // But 'getSession' fetches attempts internally to build stats.
        // I need raw attempts here to determine failures.
        // Optimally, 'getSession' could return attempts or I add 'getAttempts' to Repo.
        // I will add 'getAttempts' to Repo to be safe.
        // Wait, 'getSession' returns 'PracticeSession' which does NOT have attempts list, just stats.
        
        // Let's assume I add `getAttempts` to Repo now.
        
        // Logic for Error Bag
        val attemptsByQuestion = attempts.groupBy { it.questionId }
        
        // Find questions that are currently "failed" (last attempt was incorrect)
        val failedQuestionIds = questions.filter { q ->
            val qAttempts = attemptsByQuestion[q.id] ?: emptyList<AttemptEntity>()
            if (qAttempts.isEmpty()) false 
            else !qAttempts.maxWithOrNull(compareBy({ it.timestamp }, { it.id }))!!.isCorrect
        }.map { it.id }
        
        // Find unanswered questions
        val unansweredQuestions = questions.filter { q ->
            !attemptsByQuestion.containsKey(q.id)
        }
        
        // "Error Bag": Re-insert failures every 3 questions (configurable)
        // Check if we should insert a failure
        val totalAttempts = attempts.size
        val shouldRetry = (totalAttempts > 0) && (totalAttempts % 3 == 0) && failedQuestionIds.isNotEmpty()
        
        var nextQuestion: SessionQuestion?
        
        if (shouldRetry) {
            // Pick a failed question (prioritize oldest failure or random)
            val qId = failedQuestionIds.first()
            nextQuestion = questions.find { it.id == qId }
        } else if (unansweredQuestions.isNotEmpty()) {
            // Pick next unanswered
            nextQuestion = unansweredQuestions.first()
        } else if (failedQuestionIds.isNotEmpty()) {
            // No more new questions, but we have failures to clean up
            val qId = failedQuestionIds.first()
            nextQuestion = questions.find { it.id == qId }
        } else {
            // Done!
            return session.copy(state = SessionState.COMPLETED, currentQuestion = null)
        }
        
        return session.copy(
            state = SessionState.QUESTION,
            currentQuestion = nextQuestion
        )
    }
}
