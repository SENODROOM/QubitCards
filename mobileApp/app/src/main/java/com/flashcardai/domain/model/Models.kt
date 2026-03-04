package com.flashcardai.domain.model

data class FlashCard(
    val id: Long = 0,
    val deckId: Long,
    val question: String,
    val answer: String,
    val isMastered: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

data class Deck(
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val topic: String = "",
    val cardCount: Int = 0,
    val masteredCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val isAiGenerated: Boolean = false
)

data class QuizSession(
    val deckId: Long,
    val cards: List<FlashCard>,
    val currentIndex: Int = 0,
    val correctCount: Int = 0,
    val isCompleted: Boolean = false
)

data class QuizResult(
    val totalCards: Int,
    val masteredCards: Int,
    val score: Int  // percentage
)
