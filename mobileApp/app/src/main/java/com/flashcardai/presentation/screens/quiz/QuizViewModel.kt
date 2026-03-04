package com.flashcardai.presentation.screens.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flashcardai.data.repository.FlashCardRepository
import com.flashcardai.domain.model.Deck
import com.flashcardai.domain.model.FlashCard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuizUiState(
    val deck: Deck? = null,
    val cards: List<FlashCard> = emptyList(),
    val currentIndex: Int = 0,
    val isFlipped: Boolean = false,
    val isLoading: Boolean = true,
    val isCompleted: Boolean = false,
    val masteredInSession: Set<Long> = emptySet()
) {
    val currentCard: FlashCard? get() = cards.getOrNull(currentIndex)
    val progress: Float get() = if (cards.isEmpty()) 0f else (currentIndex + 1f) / cards.size
    val masteredCount: Int get() = cards.count { it.isMastered }
}

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val repository: FlashCardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    fun loadDeck(deckId: Long) {
        viewModelScope.launch {
            val deck = repository.getDeckById(deckId)
            val cards = repository.getCardsByDeckIdOnce(deckId).shuffled()
            _uiState.update { it.copy(deck = deck, cards = cards, isLoading = false) }
        }
    }

    fun flipCard() {
        _uiState.update { it.copy(isFlipped = !it.isFlipped) }
    }

    fun nextCard() {
        _uiState.update { state ->
            if (state.currentIndex < state.cards.size - 1) {
                state.copy(currentIndex = state.currentIndex + 1, isFlipped = false)
            } else {
                state.copy(isCompleted = true)
            }
        }
    }

    fun previousCard() {
        _uiState.update { state ->
            if (state.currentIndex > 0) {
                state.copy(currentIndex = state.currentIndex - 1, isFlipped = false)
            } else state
        }
    }

    fun toggleMastered(cardId: Long) {
        viewModelScope.launch {
            val card = _uiState.value.cards.find { it.id == cardId } ?: return@launch
            val newMastered = !card.isMastered
            repository.setMastered(cardId, newMastered)

            _uiState.update { state ->
                val updatedCards = state.cards.map {
                    if (it.id == cardId) it.copy(isMastered = newMastered) else it
                }
                val masteredInSession = if (newMastered)
                    state.masteredInSession + cardId
                else
                    state.masteredInSession - cardId
                state.copy(cards = updatedCards, masteredInSession = masteredInSession)
            }
        }
    }

    fun restartQuiz() {
        _uiState.update { state ->
            state.copy(
                currentIndex = 0,
                isFlipped = false,
                isCompleted = false,
                cards = state.cards.shuffled(),
                masteredInSession = emptySet()
            )
        }
    }

    fun resetProgress() {
        viewModelScope.launch {
            val deckId = _uiState.value.deck?.id ?: return@launch
            repository.resetDeckProgress(deckId)
            val refreshed = repository.getCardsByDeckIdOnce(deckId).shuffled()
            _uiState.update { it.copy(cards = refreshed, currentIndex = 0, isFlipped = false, isCompleted = false) }
        }
    }
}
