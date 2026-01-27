package com.studysmart.core.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.studysmart.core.data.db.entities.OptionEntity
import com.studysmart.core.data.db.entities.QuestionEntity
import com.studysmart.core.data.db.entities.QuizEntity

@Dao
interface QuizDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuiz(quiz: QuizEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: QuestionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOptions(options: List<OptionEntity>)

    @Transaction
    suspend fun insertQuizPayload(
        quiz: QuizEntity,
        questions: List<Pair<QuestionEntity, List<OptionEntity>>>
    ) {
        val quizId = insertQuiz(quiz)
        questions.forEach { (question, options) ->
            val questionId = insertQuestion(question.copy(quizId = quizId))
            insertOptions(options.map { it.copy(questionId = questionId) })
        }
    }

    @Query("SELECT * FROM quizzes WHERE id = :id")
    suspend fun getQuiz(id: Long): QuizEntity?

    @Query("SELECT * FROM questions WHERE quizId = :quizId")
    suspend fun getQuestionsForQuiz(quizId: Long): List<QuestionEntity>

    @Query("SELECT * FROM options WHERE questionId = :questionId")
    suspend fun getOptionsForQuestion(questionId: Long): List<OptionEntity>
}
