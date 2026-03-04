package com.flashcardai.presentation.screens.generate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flashcardai.data.remote.AiFlashCard
import com.flashcardai.data.repository.AiRepository
import com.flashcardai.data.repository.FlashCardRepository
import com.flashcardai.domain.model.FlashCard
import com.flashcardai.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class GenerateStep { INPUT, GENERATING, PREVIEW, SAVING }
enum class Difficulty { EASY, MEDIUM, HARD }

data class GenerateUiState(
    val topic: String = "",
    val cardCount: Int = 10,
    val difficulty: Difficulty = Difficulty.MEDIUM,
    val step: GenerateStep = GenerateStep.INPUT,
    val generatedCards: List<AiFlashCard> = emptyList(),
    val errorMessage: String? = null,
    val savedDeckId: Long? = null,
    val deckTitle: String = ""
)

@HiltViewModel
class GenerateViewModel @Inject constructor(
    private val aiRepository: AiRepository,
    private val flashCardRepository: FlashCardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GenerateUiState())
    val uiState: StateFlow<GenerateUiState> = _uiState.asStateFlow()

    fun updateTopic(topic: String) {
        _uiState.update { it.copy(topic = topic, errorMessage = null) }
    }

    fun updateCardCount(count: Int) {
        _uiState.update { it.copy(cardCount = count) }
    }

    fun updateDifficulty(difficulty: Difficulty) {
        _uiState.update { it.copy(difficulty = difficulty) }
    }

    fun updateDeckTitle(title: String) {
        _uiState.update { it.copy(deckTitle = title) }
    }

    fun generateCards() {
        val topic = _uiState.value.topic.trim()
        if (topic.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter a topic first") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(step = GenerateStep.GENERATING, errorMessage = null) }

            val result = aiRepository.generateFlashCards(
                topic = topic,
                count = _uiState.value.cardCount,
                difficulty = _uiState.value.difficulty.name.lowercase()
            )

            when (result) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            step = GenerateStep.PREVIEW,
                            generatedCards = result.data,
                            deckTitle = it.topic.replaceFirstChar { c -> c.uppercase() }
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(step = GenerateStep.INPUT, errorMessage = result.message)
                    }
                }
                else -> {}
            }
        }
    }

    fun removePreviewCard(index: Int) {
        _uiState.update {
            it.copy(generatedCards = it.generatedCards.toMutableList().apply { removeAt(index) })
        }
    }

    fun saveDeck() {
        viewModelScope.launch {
            val state = _uiState.value
            _uiState.update { it.copy(step = GenerateStep.SAVING) }

            val deckId = flashCardRepository.createDeck(
                title = state.deckTitle.ifBlank { state.topic },
                topic = state.topic,
                isAiGenerated = true
            )

            val cards = state.generatedCards.map { ai ->
                FlashCard(deckId = deckId, question = ai.question, answer = ai.answer)
            }
            flashCardRepository.addCards(cards)

            _uiState.update { it.copy(savedDeckId = deckId) }
        }
    }

    fun resetState() {
        _uiState.value = GenerateUiState()
    }

    fun goBackToInput() {
        _uiState.update { it.copy(step = GenerateStep.INPUT, generatedCards = emptyList()) }
    }
}
