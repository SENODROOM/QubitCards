package com.flashcardai.data.repository

import com.flashcardai.BuildConfig
import com.flashcardai.data.remote.AiFlashCard
import com.flashcardai.data.remote.GeminiApiService
import com.flashcardai.data.remote.GeminiContent
import com.flashcardai.data.remote.GeminiPart
import com.flashcardai.data.remote.GeminiRequest
import com.flashcardai.utils.Resource
import org.json.JSONArray
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiRepository @Inject constructor(
    private val geminiApiService: GeminiApiService
) {

    suspend fun generateFlashCards(
        topic: String,
        count: Int = 10,
        difficulty: String = "medium"
    ): Resource<List<AiFlashCard>> {
        return try {
            val prompt = buildPrompt(topic, count, difficulty)
            val request = GeminiRequest(
                contents = listOf(
                    GeminiContent(parts = listOf(GeminiPart(text = prompt)))
                )
            )

            val response = geminiApiService.generateContent(
                apiKey = BuildConfig.GEMINI_API_KEY,
                request = request
            )

            if (response.error != null) {
                return Resource.Error("AI Error: ${response.error.message}")
            }

            val rawText = response.candidates
                ?.firstOrNull()
                ?.content
                ?.parts
                ?.firstOrNull()
                ?.text
                ?: return Resource.Error("No response from AI")

            val cards = parseFlashCards(rawText)
            if (cards.isEmpty()) {
                Resource.Error("Could not parse flashcards from AI response")
            } else {
                Resource.Success(cards)
            }

        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error occurred")
        }
    }

    private fun buildPrompt(topic: String, count: Int, difficulty: String): String {
        return """
            You are an expert educational content creator. Generate exactly $count flashcards about "$topic" at $difficulty difficulty level.
            
            IMPORTANT: Respond ONLY with a valid JSON array. No extra text, no markdown, no explanation.
            
            Format:
            [
              {"question": "Question text here?", "answer": "Clear, concise answer here."},
              {"question": "Another question?", "answer": "Another answer."}
            ]
            
            Requirements:
            - Questions should be clear and specific
            - Answers should be concise (1-3 sentences)
            - Cover different aspects of the topic
            - Make them educational and accurate
            - Difficulty: $difficulty (easy=basic facts, medium=understanding, hard=analysis/application)
            
            Generate exactly $count flashcard pairs now:
        """.trimIndent()
    }

    private fun parseFlashCards(rawText: String): List<AiFlashCard> {
        return try {
            // Extract JSON array from response
            val jsonStart = rawText.indexOf('[')
            val jsonEnd = rawText.lastIndexOf(']')
            if (jsonStart == -1 || jsonEnd == -1) return emptyList()

            val jsonStr = rawText.substring(jsonStart, jsonEnd + 1)
            val jsonArray = JSONArray(jsonStr)
            val cards = mutableListOf<AiFlashCard>()

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val question = obj.optString("question", "").trim()
                val answer = obj.optString("answer", "").trim()
                if (question.isNotEmpty() && answer.isNotEmpty()) {
                    cards.add(AiFlashCard(question = question, answer = answer))
                }
            }
            cards
        } catch (e: Exception) {
            emptyList()
        }
    }
}
