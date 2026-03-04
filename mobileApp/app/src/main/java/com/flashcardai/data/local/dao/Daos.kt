package com.flashcardai.data.local.dao

import androidx.room.*
import com.flashcardai.data.local.entity.DeckEntity
import com.flashcardai.data.local.entity.FlashCardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DeckDao {
    @Query("SELECT * FROM decks ORDER BY createdAt DESC")
    fun getAllDecks(): Flow<List<DeckEntity>>

    @Query("SELECT * FROM decks WHERE id = :id")
    suspend fun getDeckById(id: Long): DeckEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeck(deck: DeckEntity): Long

    @Update
    suspend fun updateDeck(deck: DeckEntity)

    @Delete
    suspend fun deleteDeck(deck: DeckEntity)

    @Query("DELETE FROM decks WHERE id = :id")
    suspend fun deleteDeckById(id: Long)
}

@Dao
interface FlashCardDao {
    @Query("SELECT * FROM flashcards WHERE deckId = :deckId ORDER BY createdAt ASC")
    fun getCardsByDeckId(deckId: Long): Flow<List<FlashCardEntity>>

    @Query("SELECT * FROM flashcards WHERE deckId = :deckId ORDER BY createdAt ASC")
    suspend fun getCardsByDeckIdOnce(deckId: Long): List<FlashCardEntity>

    @Query("SELECT COUNT(*) FROM flashcards WHERE deckId = :deckId")
    suspend fun getCardCountForDeck(deckId: Long): Int

    @Query("SELECT COUNT(*) FROM flashcards WHERE deckId = :deckId AND isMastered = 1")
    suspend fun getMasteredCountForDeck(deckId: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: FlashCardEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCards(cards: List<FlashCardEntity>)

    @Update
    suspend fun updateCard(card: FlashCardEntity)

    @Delete
    suspend fun deleteCard(card: FlashCardEntity)

    @Query("UPDATE flashcards SET isMastered = :mastered WHERE id = :cardId")
    suspend fun setMastered(cardId: Long, mastered: Boolean)

    @Query("UPDATE flashcards SET isMastered = 0 WHERE deckId = :deckId")
    suspend fun resetDeckProgress(deckId: Long)
}
