package com.flashcardai.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flashcardai.data.repository.FlashCardRepository
import com.flashcardai.domain.model.Deck
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val decks: List<Deck> = emptyList(),
    val isLoading: Boolean = false,
    val deletedDeckMessage: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: FlashCardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllDecks()
                .catch { _uiState.update { it.copy(isLoading = false) } }
                .collect { decks ->
                    _uiState.update { it.copy(decks = decks, isLoading = false) }
                }
        }
    }

    fun deleteDeck(deck: Deck) {
        viewModelScope.launch {
            repository.deleteDeck(deck.id)
            _uiState.update { it.copy(deletedDeckMessage = "\"${deck.title}\" deleted") }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(deletedDeckMessage = null) }
    }
}
