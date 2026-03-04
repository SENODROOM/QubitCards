package com.flashcardai.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.flashcardai.data.local.dao.DeckDao
import com.flashcardai.data.local.dao.FlashCardDao
import com.flashcardai.data.local.entity.DeckEntity
import com.flashcardai.data.local.entity.FlashCardEntity

@Database(
    entities = [DeckEntity::class, FlashCardEntity::class],
    version = 1,
    exportSchema = false
)
abstract class FlashCardDatabase : RoomDatabase() {
    abstract fun deckDao(): DeckDao
    abstract fun flashCardDao(): FlashCardDao

    companion object {
        const val DATABASE_NAME = "flashcard_db"
    }
}
