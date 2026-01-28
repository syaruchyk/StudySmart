package com.studysmart.features.generatequiz.domain

import com.studysmart.core.data.db.dao.DocumentDao
import com.studysmart.core.data.db.dao.QuizDao
import com.studysmart.core.data.db.entities.OptionEntity
import com.studysmart.core.data.db.entities.QuestionEntity
import com.studysmart.core.data.db.entities.QuizEntity
import com.studysmart.features.generatequiz.data.GeminiDataSource
import com.studysmart.features.generatequiz.data.KtorGeminiDataSource
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// Internal models for parsing LLM response
@Serializable
private data class QuizJson(
    val title: String,
    val questions: List<QuestionJson>
)

@Serializable
private data class QuestionJson(
    val text: String,
    val type: String, // "SINGLE_CHOICE", "TRUE_FALSE"
    val options: List<OptionJson>,
    val explanation: String? = null
)

@Serializable
private data class OptionJson(
    val text: String,
    val isCorrect: Boolean
)

class GenerateQuizUseCase(
    private val documentDao: DocumentDao,
    private val quizDao: QuizDao,
    private val geminiDataSource: GeminiDataSource = KtorGeminiDataSource()
) {

    private val jsonParser = Json { 
        ignoreUnknownKeys = true 
        isLenient = true
    }

    suspend fun invoke(documentId: Long, apiKey: String): Long {
        // 1. Fetch chunks
        val chunks = documentDao.getChunksForDocument(documentId)
        if (chunks.isEmpty()) throw IllegalStateException("Document has no text chunks to generate quiz from.")

        val textChunks = chunks.map { it.content }.take(20) // Limit to 20 chunks for now to avoid token limits

        // 2. Build Prompt
        val prompt = QuizPromptBuilder.build(textChunks)

        // 3. Call LLM
        val jsonResponseOriginal = geminiDataSource.generateQuizJson(prompt, apiKey)
        
        // Cleanup markdown code blocks if present
        val jsonCleaned = jsonResponseOriginal
            .replace("```json", "")
            .replace("```", "")
            .trim()

        // 4. Parse
        val quizJson = try {
            jsonParser.decodeFromString<QuizJson>(jsonCleaned)
        } catch (e: Exception) {
            throw IllegalStateException("Failed to parse Gemini response: ${e.message}. \nResponse: $jsonCleaned", e)
        }

        // 5. Map to Entities and Save
        val quizEntity = QuizEntity(
            title = quizJson.title,
            documentId = documentId,
            generatedAt = System.currentTimeMillis()
        )

        val questionsPayload = quizJson.questions.map { qJson ->
            val questionEntity = QuestionEntity(
                quizId = 0, // Set by DAO
                text = qJson.text,
                explanation = qJson.explanation
            )
            
            val optionEntities = qJson.options.map { oJson ->
                OptionEntity(
                    questionId = 0,
                    text = oJson.text,
                    isCorrect = oJson.isCorrect
                )
            }
            
            Pair(questionEntity, optionEntities)
        }

        // 6. Transactional Insert
        quizDao.insertQuizPayload(quizEntity, questionsPayload)
        
        // Fetch to return ID
        val createdQuiz = quizDao.getQuizzesForDocument(documentId)
            .maxByOrNull { it.generatedAt } 
            ?: throw IllegalStateException("Quiz saved but not found?")
            
        return createdQuiz.id
    }
}
