package com.flashcardai.data.repository

import com.flashcardai.data.local.dao.DeckDao
import com.flashcardai.data.local.dao.FlashCardDao
import com.flashcardai.data.local.entity.DeckEntity
import com.flashcardai.data.local.entity.FlashCardEntity
import com.flashcardai.domain.model.Deck
import com.flashcardai.domain.model.FlashCard
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlashCardRepository @Inject constructor(
    private val deckDao: DeckDao,
    private val flashCardDao: FlashCardDao
) {

    // ─── Decks ────────────────────────────────────────────────────────────

    fun getAllDecks(): Flow<List<Deck>> = deckDao.getAllDecks().map { entities ->
        entities.map { entity ->
            val cardCount = flashCardDao.getCardCountForDeck(entity.id)
            val masteredCount = flashCardDao.getMasteredCountForDeck(entity.id)
            entity.toDomain(cardCount, masteredCount)
        }
    }

    suspend fun getDeckById(id: Long): Deck? {
        val entity = deckDao.getDeckById(id) ?: return null
        val cardCount = flashCardDao.getCardCountForDeck(id)
        val masteredCount = flashCardDao.getMasteredCountForDeck(id)
        return entity.toDomain(cardCount, masteredCount)
    }

    suspend fun createDeck(title: String, description: String = "", topic: String = "", isAiGenerated: Boolean = false): Long {
        return deckDao.insertDeck(
            DeckEntity(title = title, description = description, topic = topic, isAiGenerated = isAiGenerated)
        )
    }

    suspend fun updateDeck(deck: Deck) {
        deckDao.updateDeck(deck.toEntity())
    }

    suspend fun deleteDeck(id: Long) {
        deckDao.deleteDeckById(id)
    }

    // ─── Cards ────────────────────────────────────────────────────────────

    fun getCardsByDeckId(deckId: Long): Flow<List<FlashCard>> =
        flashCardDao.getCardsByDeckId(deckId).map { it.map(FlashCardEntity::toDomain) }

    suspend fun getCardsByDeckIdOnce(deckId: Long): List<FlashCard> =
        flashCardDao.getCardsByDeckIdOnce(deckId).map(FlashCardEntity::toDomain)

    suspend fun addCard(card: FlashCard): Long =
        flashCardDao.insertCard(card.toEntity())

    suspend fun addCards(cards: List<FlashCard>) =
        flashCardDao.insertCards(cards.map(FlashCard::toEntity))

    suspend fun updateCard(card: FlashCard) =
        flashCardDao.updateCard(card.toEntity())

    suspend fun deleteCard(card: FlashCard) =
        flashCardDao.deleteCard(card.toEntity())

    suspend fun setMastered(cardId: Long, mastered: Boolean) =
        flashCardDao.setMastered(cardId, mastered)

    suspend fun resetDeckProgress(deckId: Long) =
        flashCardDao.resetDeckProgress(deckId)
}

// ─── Mappers ──────────────────────────────────────────────────────────────

private fun DeckEntity.toDomain(cardCount: Int, masteredCount: Int) = Deck(
    id = id, title = title, description = description, topic = topic,
    cardCount = cardCount, masteredCount = masteredCount,
    createdAt = createdAt, isAiGenerated = isAiGenerated
)

private fun Deck.toEntity() = DeckEntity(
    id = id, title = title, description = description, topic = topic,
    createdAt = createdAt, isAiGenerated = isAiGenerated
)

private fun FlashCardEntity.toDomain() = FlashCard(
    id = id, deckId = deckId, question = question, answer = answer,
    isMastered = isMastered, createdAt = createdAt
)

private fun FlashCard.toEntity() = FlashCardEntity(
    id = id, deckId = deckId, question = question, answer = answer,
    isMastered = isMastered, createdAt = createdAt
)
