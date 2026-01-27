package com.studysmart.core.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.studysmart.core.data.db.entities.AttemptEntity
import com.studysmart.core.data.db.entities.QuestionEntity
import com.studysmart.core.data.db.entities.SessionEntity
import com.studysmart.core.data.db.entities.SrsStateEntity

@Dao
interface PracticeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: SessionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttempt(attempt: AttemptEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSrsState(srsState: SrsStateEntity)

    @Query("""
        SELECT q.* FROM questions q
        INNER JOIN srs_states s ON q.id = s.questionId
        WHERE s.dueAt <= :now
        ORDER BY s.dueAt ASC
    """)
    suspend fun getDueQuestions(now: Long): List<QuestionEntity>

    @Query("""
        SELECT * FROM attempts 
        WHERE isCorrect = 0 
        ORDER BY timestamp DESC 
        LIMIT :limit
    """)
    suspend fun getRecentFailures(limit: Int): List<AttemptEntity>

    // Simplified stats query example
    @Query("""
        SELECT q.quizId as topicId, COUNT(*) as totalAttempts, SUM(CASE WHEN a.isCorrect THEN 1 ELSE 0 END) as correctAttempts
        FROM attempts a
        INNER JOIN questions q ON a.questionId = q.id
        GROUP BY q.quizId
    """)
    suspend fun getStatsByTopic(): List<TopicStats>
}

data class TopicStats(
    val topicId: Long,
    val totalAttempts: Int,
    val correctAttempts: Int
)
