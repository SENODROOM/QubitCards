package com.flashcardai.presentation.screens.manage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flashcardai.data.repository.FlashCardRepository
import com.flashcardai.domain.model.Deck
import com.flashcardai.domain.model.FlashCard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ManageUiState(
    val deck: Deck? = null,
    val cards: List<FlashCard> = emptyList(),
    val isLoading: Boolean = true,
    val showAddDialog: Boolean = false,
    val editingCard: FlashCard? = null,
    val message: String? = null
)

@HiltViewModel
class ManageCardsViewModel @Inject constructor(
    private val repository: FlashCardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManageUiState())
    val uiState: StateFlow<ManageUiState> = _uiState.asStateFlow()

    private var deckId: Long = -1

    fun loadDeck(id: Long) {
        deckId = id
        viewModelScope.launch {
            val deck = repository.getDeckById(id)
            _uiState.update { it.copy(deck = deck) }
        }
        viewModelScope.launch {
            repository.getCardsByDeckId(id)
                .catch { }
                .collect { cards ->
                    _uiState.update { it.copy(cards = cards, isLoading = false) }
                }
        }
    }

    fun showAddDialog() { _uiState.update { it.copy(showAddDialog = true, editingCard = null) } }
    fun showEditDialog(card: FlashCard) { _uiState.update { it.copy(showAddDialog = true, editingCard = card) } }
    fun dismissDialog() { _uiState.update { it.copy(showAddDialog = false, editingCard = null) } }

    fun saveCard(question: String, answer: String) {
        viewModelScope.launch {
            val editing = _uiState.value.editingCard
            if (editing != null) {
                repository.updateCard(editing.copy(question = question, answer = answer))
                _uiState.update { it.copy(message = "Card updated") }
            } else {
                repository.addCard(FlashCard(deckId = deckId, question = question, answer = answer))
                _uiState.update { it.copy(message = "Card added") }
            }
            dismissDialog()
        }
    }

    fun deleteCard(card: FlashCard) {
        viewModelScope.launch {
            repository.deleteCard(card)
            _uiState.update { it.copy(message = "Card deleted") }
        }
    }

    fun clearMessage() { _uiState.update { it.copy(message = null) } }
}
