package com.studysmart.core.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.studysmart.core.data.db.dao.DocumentDao
import com.studysmart.core.data.db.dao.EventJournalDao
import com.studysmart.core.data.db.dao.PracticeDao
import com.studysmart.core.data.db.dao.QuizDao
import com.studysmart.core.data.db.entities.AttemptEntity
import com.studysmart.core.data.db.entities.ChunkEntity
import com.studysmart.core.data.db.entities.DocumentEntity
import com.studysmart.core.data.db.entities.EventJournalEntity
import com.studysmart.core.data.db.entities.OptionEntity
import com.studysmart.core.data.db.entities.QuestionEntity
import com.studysmart.core.data.db.entities.QuizEntity
import com.studysmart.core.data.db.entities.SessionEntity
import com.studysmart.core.data.db.entities.SessionSnapshotEntity
import com.studysmart.core.data.db.entities.SrsStateEntity

@Database(
    entities = [
        DocumentEntity::class,
        ChunkEntity::class,
        QuizEntity::class,
        QuestionEntity::class,
        OptionEntity::class,
        SessionEntity::class,
        AttemptEntity::class,
        SrsStateEntity::class,
        EventJournalEntity::class,
        SessionSnapshotEntity::class
    ],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun documentDao(): DocumentDao
    abstract fun quizDao(): QuizDao
    abstract fun practiceDao(): PracticeDao
    abstract fun eventJournalDao(): EventJournalDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "studysmart_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
