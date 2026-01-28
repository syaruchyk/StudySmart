package com.studysmart.features.generatequiz.domain

object QuizPromptBuilder {
    fun build(textChunks: List<String>): String {
        // Concatenate chunks (limit total length if needed, for now assume reasonable size)
        val fullText = textChunks.joinToString("\n\n")
        
        return """
            You are a teacher creating a quiz from the following text.
            Capture the most important concepts.
            Return ONLY a valid JSON object. Do not include markdown formatting (like ```json ... ```) or any preamble.
            
            The JSON structure must be:
            {
              "title": "Quiz Title",
              "questions": [
                {
                  "text": "Question text?",
                  "type": "SINGLE_CHOICE",
                  "options": [
                    { "text": "Option A", "isCorrect": true },
                    { "text": "Option B", "isCorrect": false },
                    { "text": "Option C", "isCorrect": false },
                    { "text": "Option D", "isCorrect": false }
                  ],
                  "explanation": "Explanation here"
                }
              ]
            }
            
            TEXT:
            $fullText
        """.trimIndent()
    }
}
