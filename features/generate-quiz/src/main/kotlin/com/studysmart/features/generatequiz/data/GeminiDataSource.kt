package com.studysmart.features.generatequiz.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import com.studysmart.core.domain.telemetry.TelemetryClient


@Serializable
data class GeminiRequest(
    val contents: List<Content>
)

@Serializable
data class Content(
    val parts: List<Part>
)

@Serializable
data class Part(
    val text: String
)

@Serializable
data class GeminiResponse(
    val candidates: List<Candidate>?
)

@Serializable
data class Candidate(
    val content: Content?,
    val finishReason: String?
)

interface GeminiDataSource {
    suspend fun generateQuizJson(prompt: String, apiKey: String): String
}

class KtorGeminiDataSource(
    private val telemetry: TelemetryClient
) : GeminiDataSource {

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
    }

    override suspend fun generateQuizJson(prompt: String, apiKey: String): String {
        val startTime = System.currentTimeMillis()
        telemetry.sendEvent("quiz_generation_started")

        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey"
        
        val requestBody = GeminiRequest(
            contents = listOf(
                Content(parts = listOf(Part(text = prompt)))
            )
        )

        try {
            val response: GeminiResponse = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

            val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text 
                ?: throw Exception("No content in Gemini response")

            telemetry.sendEvent("quiz_generation_completed", mapOf(
                "duration_ms" to (System.currentTimeMillis() - startTime)
            ))

            return text
                
        } catch (e: Exception) {
            telemetry.sendEvent("quiz_generation_failed", mapOf(
                "error_type" to (e::class.simpleName ?: "Unknown")
            ))
            throw Exception("Gemini API Call Failed: ${e.message}", e)
        }
    }
}
