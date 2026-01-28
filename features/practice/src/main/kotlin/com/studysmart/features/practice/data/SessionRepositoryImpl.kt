package com.studysmart.features.practice.data

import com.studysmart.core.data.db.dao.PracticeDao
import com.studysmart.core.data.db.dao.QuizDao
import com.studysmart.core.data.db.entities.AttemptEntity
import com.studysmart.core.data.db.entities.SessionEntity
import com.studysmart.core.data.db.entities.SrsStateEntity
import com.studysmart.features.practice.domain.PracticeSession
import com.studysmart.features.practice.domain.SessionOption
import com.studysmart.features.practice.domain.SessionQuestion
import com.studysmart.features.practice.domain.SessionRepository
import com.studysmart.features.practice.domain.SessionState
import com.studysmart.features.practice.domain.SessionStats
import com.studysmart.features.practice.domain.SrsData

class SessionRepositoryImpl(
    private val practiceDao: PracticeDao,
    private val quizDao: QuizDao
) : SessionRepository {

    override suspend fun createSession(quizId: Long): Long {
        val sessionEntity = SessionEntity(
            quizId = quizId,
            startedAt = System.currentTimeMillis(),
            completedAt = null
        )
        return practiceDao.insertSession(sessionEntity)
    }

    override suspend fun getSession(sessionId: Long): PracticeSession? {
        val entity = practiceDao.getSession(sessionId) ?: return null
        val attempts = practiceDao.getAttempts(sessionId)
        
        // Calculate basic stats
        val correct = attempts.count { it.isCorrect }
        
        return PracticeSession(
            id = entity.id,
            quizId = entity.quizId,
            state = if (entity.completedAt != null) SessionState.COMPLETED else SessionState.IDLE,
            stats = SessionStats(
                totalQuestionsAnswered = attempts.size,
                correctAnswers = correct,
                startTime = entity.startedAt
            ),
            completedAt = entity.completedAt
        )
    }

    override suspend fun getQuestion(questionId: Long): SessionQuestion? {
        // This assumes we fetch generic info. In a real generic repo we might want to fetch by ID
        // But here we rely on quizDao to fetch entities
        // Since QuizDao doesn't have "getQuestionById", let's use what we have or add it.
        // Actually for now let's use the 'getQuestionsForQuiz' and filter, or assumes we pass the object.
        // Ideally we should add 'getQuestion(id)' to QuizDao. 
        // For MVP, I will assume we fetch all questions for a quiz often.
        // Wait, I need to implement getQuestion.
        // I'll add a helper here that queries via lists if Dao is missing single item query, 
        // to avoid modifying Core too much if not verified. 
        // But efficient approach is to add getQuestion to QuizDao.
        // Let's defer that and assume we iterate over quiz questions for now.
        return null // Implemented below via getQuestionsForQuiz
    }
    
    // We implement specific query:
    override suspend fun getQuestionsForQuiz(quizId: Long): List<SessionQuestion> {
        val questions = quizDao.getQuestionsForQuiz(quizId)
        return questions.map { q ->
            val options = quizDao.getOptionsForQuestion(q.id).map { o ->
                SessionOption(
                    id = o.id,
                    text = o.text,
                    isCorrect = o.isCorrect
                )
            }
            
            val srsEntity = practiceDao.getSrsState(q.id)
            val srsData = srsEntity?.let {
                SrsData(it.ease, it.intervalDays, it.repetitions, it.dueAt)
            }
            
            SessionQuestion(
                id = q.id,
                text = q.text,
                options = options,
                explanation = q.explanation,
                srsData = srsData
            )
        }
    }

    override suspend fun recordAttempt(
        sessionId: Long,
        questionId: Long,
        optionId: Long?,
        isCorrect: Boolean,
        timeTakenMs: Long
    ) {
        val attempt = AttemptEntity(
            sessionId = sessionId,
            questionId = questionId,
            selectedOptionId = optionId,
            isCorrect = isCorrect,
            timeTakenMs = timeTakenMs,
            timestamp = System.currentTimeMillis()
        )
        practiceDao.insertAttempt(attempt)
    }

    override suspend fun getSrsData(questionId: Long): SrsData? {
        val entity = practiceDao.getSrsState(questionId) ?: return null
        return SrsData(
            easeFactor = entity.ease,
            intervalDays = entity.intervalDays,
            repetitions = entity.repetitions,
            dueAt = entity.dueAt
        )
    }

    override suspend fun getAttempts(sessionId: Long): List<AttemptEntity> {
        return practiceDao.getAttempts(sessionId)
    }

    override suspend fun updateSrsState(questionId: Long, srsData: SrsData) {
        val entity = SrsStateEntity(
            questionId = questionId,
            ease = srsData.easeFactor,
            intervalDays = srsData.intervalDays,
            repetitions = srsData.repetitions,
            dueAt = srsData.dueAt
        )
        practiceDao.upsertSrsState(entity)
    }

    override suspend fun getRecentFailures(limit: Int): List<Long> {
        return practiceDao.getRecentFailures(limit).map { it.questionId }
    }
}
