package com.studysmart.core.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sessions",
    foreignKeys = [
        ForeignKey(
            entity = QuizEntity::class,
            parentColumns = ["id"],
            childColumns = ["quizId"]
        )
    ],
    indices = [Index(value = ["quizId"], name = "index_session_quizId")]
)
data class SessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val quizId: Long,
    val startedAt: Long,
    val completedAt: Long?
)

@Entity(
    tableName = "attempts",
    foreignKeys = [
        ForeignKey(
            entity = SessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"]
        ),
        ForeignKey(
            entity = QuestionEntity::class,
            parentColumns = ["id"],
            childColumns = ["questionId"]
        )
    ],
    indices = [
        Index(value = ["sessionId"], name = "index_attempt_sessionId"),
        Index(value = ["questionId"], name = "index_attempt_questionId")
    ]
)
data class AttemptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val questionId: Long,
    val selectedOptionId: Long?,
    val isCorrect: Boolean,
    val timeTakenMs: Long,
    val timestamp: Long
)

// SM-2 SRS State
@Entity(
    tableName = "srs_states",
    foreignKeys = [
        ForeignKey(
            entity = QuestionEntity::class,
            parentColumns = ["id"],
            childColumns = ["questionId"]
        )
    ],
    indices = [Index(value = ["dueAt"], name = "index_srs_dueAt")]
)
data class SrsStateEntity(
    @PrimaryKey val questionId: Long,
    val ease: Double,
    val intervalDays: Int,
    val repetitions: Int,
    val dueAt: Long
)
