package com.studysmart.features.generatequiz.domain

import com.studysmart.core.data.db.dao.DocumentDao
import com.studysmart.core.data.db.dao.QuizDao
import com.studysmart.core.data.db.entities.ChunkEntity
import com.studysmart.core.data.db.entities.QuizEntity
import com.studysmart.features.generatequiz.data.GeminiDataSource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GenerateQuizUseCaseTest {

    private val documentDao = mockk<DocumentDao>()
    private val quizDao = mockk<QuizDao>(relaxed = true)
    private val geminiDataSource = mockk<GeminiDataSource>()
    private val useCase = GenerateQuizUseCase(documentDao, quizDao, geminiDataSource)

    @Test
    fun `invoke generates and saves quiz`() = runTest {
        // Arrange
        val docId = 123L
        val chunks = listOf(
            ChunkEntity(1, docId, "Page 1 content", 1, 0, 100)
        )
        coEvery { documentDao.getChunksForDocument(docId) } returns chunks
        
        val llmResponse = """
            ```json
            {
              "title": "Sample Quiz",
              "questions": [
                {
                  "text": "What is 2+2?",
                  "type": "SINGLE_CHOICE",
                  "options": [
                    { "text": "4", "isCorrect": true },
                    { "text": "5", "isCorrect": false }
                  ],
                  "explanation": "Math"
                }
              ]
            }
            ```
        """.trimIndent()
        coEvery { geminiDataSource.generateQuizJson(any(), any()) } returns llmResponse
        
        val savedQuiz = QuizEntity(id = 55L, documentId = docId, generatedAt = 1000L, title = "Sample Quiz")
        coEvery { quizDao.getQuizzesForDocument(docId) } returns listOf(savedQuiz)

        // Act
        val resultQuizId = useCase.invoke(docId, "dummy_key")

        // Assert
        assertEquals(55L, resultQuizId)
        
        coVerify { 
            geminiDataSource.generateQuizJson(any(), "dummy_key")
            quizDao.insertQuizPayload(
                match { it.title == "Sample Quiz" && it.documentId == docId },
                match { it.size == 1 }
            )
        }
    }
}
