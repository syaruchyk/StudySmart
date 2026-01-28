package com.studysmart.features.practice.domain

import com.studysmart.core.data.db.entities.AttemptEntity
import com.studysmart.core.data.db.entities.SrsStateEntity
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import java.util.concurrent.atomic.AtomicLong

// Mock Repository for Integration Test
class FakeSessionRepository : SessionRepository {
    private val sessions = mutableMapOf<Long, PracticeSession>()
    private val questions = mutableListOf<SessionQuestion>()
    private val attempts = mutableListOf<AttemptEntity>()
    private val srsStates = mutableMapOf<Long, SrsData>()
    private var currentTime = 1000L // Monotonic clock for tests

    
    // Test Helpers
    fun addQuestion(q: SessionQuestion) {
        questions.add(q)
    }

    override suspend fun createSession(quizId: Long): Long {
        val id = System.currentTimeMillis()
        sessions[id] = PracticeSession(id, quizId)
        return id
    }

    override suspend fun getSession(sessionId: Long): PracticeSession? {
        val session = sessions[sessionId] ?: return null
        val sessionAttempts = attempts.filter { it.sessionId == sessionId }
        val correct = sessionAttempts.count { it.isCorrect }
        return session.copy(
            stats = SessionStats(sessionAttempts.size, correct)
        )
    }

    override suspend fun getQuestion(questionId: Long): SessionQuestion? {
        // Not used heavily, we use getQuestionsForQuiz
        return questions.find { it.id == questionId }
    }

    override suspend fun getQuestionsForQuiz(quizId: Long): List<SessionQuestion> {
        return questions // Assume all mock questions belong to quiz
    }

    override suspend fun getAttempts(sessionId: Long): List<AttemptEntity> {
        return attempts.filter { it.sessionId == sessionId }
    }

    override suspend fun recordAttempt(
        sessionId: Long,
        questionId: Long,
        optionId: Long?,
        isCorrect: Boolean,
        timeTakenMs: Long
    ) {
        attempts.add(
            AttemptEntity(
                id = System.nanoTime(),
                sessionId = sessionId,
                questionId = questionId,
                selectedOptionId = optionId,
                isCorrect = isCorrect,
                timeTakenMs = timeTakenMs,
                timestamp = currentTime++
            )
        )
    }

    override suspend fun getSrsData(questionId: Long): SrsData? {
        return srsStates[questionId]
    }

    override suspend fun updateSrsState(questionId: Long, srsData: SrsData) {
        srsStates[questionId] = srsData
    }

    override suspend fun getRecentFailures(limit: Int): List<Long> {
        return emptyList()
    }
}

class SessionFlowIntegrationTest {

    @Test
    fun `full session flow - start, answer, complete`() = runBlocking {
        // Setup
        val repo = FakeSessionRepository()
        // Add 2 questions
        val q1 = SessionQuestion(1, "Q1", listOf(SessionOption(10, "A", true), SessionOption(11, "B", false)), null)
        val q2 = SessionQuestion(2, "Q2", listOf(SessionOption(20, "C", true), SessionOption(21, "D", false)), null)
        repo.addQuestion(q1)
        repo.addQuestion(q2)

        val updateSrs = UpdateSrsUseCase(repo)
        val startSession = StartSessionUseCase(repo)
        val answerQuestion = AnswerQuestionUseCase(repo, updateSrs)
        val getNext = GetNextQuestionUseCase(repo)

        // 1. Start Session
        var session = startSession(100L)
        assertEquals(SessionState.QUESTION, session.state)
        assertEquals(1L, session.currentQuestion?.id)

        // 2. Answer Q1 Correctly
        session = answerQuestion(session, 10L, 1000)
        assertEquals(SessionState.ANSWERED, session.state)
        assertEquals(1, session.stats.correctAnswers)
        
        // Verify SRS updated
        val srsQ1 = repo.getSrsData(1L)
        assertNotNull(srsQ1)
        assertTrue(srsQ1!!.intervalDays >= 1)

        // 3. Next Question
        session = getNext(session)
        assertEquals(SessionState.QUESTION, session.state)
        assertEquals(2L, session.currentQuestion?.id)

        // 4. Answer Q2 Incorrectly
        session = answerQuestion(session, 21L, 2000)
        assertEquals(SessionState.ANSWERED, session.state)
        assertEquals(1, session.stats.correctAnswers) // Still 1

        // 5. Next Question (Error Bag Check)
        // Since we have < 3 attempts and mixed results, implementation might pick next unanswered or failure.
        // Our 'GetNextQuestionUseCase' logic:
        // failures = [Q2]
        // unanswered = []
        // shouldRetry = attempts(2) % 3 == 0 (False)
        // But unanswered is empty.
        // So failedQuestionIds is not empty (Q2).
        // Should pick Q2 again immediately (or after bag interval?).
        // Logic: "Else if failedQuestionIds.isNotEmpty -> Pick failure".
        
        session = getNext(session)
        assertEquals(SessionState.QUESTION, session.state)
        assertEquals(2L, session.currentQuestion?.id) // Should be Q2 again

        // 6. Answer Q2 Correctly
        session = answerQuestion(session, 20L, 1000)
        session = getNext(session)

        // 7. Complete
        // All answered/corrected?
        // Q1: Correct (latest)
        // Q2: Correct (latest)
        // No failures, no unanswered.
        assertEquals(SessionState.COMPLETED, session.state)
    }
}
