package com.flashcardai.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

// ─── Gemini API Data Models ────────────────────────────────────────────────

data class GeminiRequest(
    val contents: List<GeminiContent>,
    @SerializedName("generationConfig")
    val generationConfig: GenerationConfig = GenerationConfig()
)

data class GeminiContent(
    val parts: List<GeminiPart>,
    val role: String = "user"
)

data class GeminiPart(val text: String)

data class GenerationConfig(
    val temperature: Float = 0.7f,
    @SerializedName("maxOutputTokens")
    val maxOutputTokens: Int = 2048,
    @SerializedName("topP")
    val topP: Float = 0.9f
)

data class GeminiResponse(
    val candidates: List<GeminiCandidate>?,
    val error: GeminiError?
)

data class GeminiCandidate(
    val content: GeminiContent?,
    @SerializedName("finishReason")
    val finishReason: String?
)

data class GeminiError(
    val code: Int,
    val message: String,
    val status: String
)

// ─── Parsed Flashcard from AI ─────────────────────────────────────────────

data class AiFlashCard(
    val question: String,
    val answer: String
)

// ─── Retrofit Interface ───────────────────────────────────────────────────

interface GeminiApiService {
    @POST("v1beta/models/gemini-1.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}
