package com.studysmart.core.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.studysmart.core.data.db.entities.EventJournalEntity

@Dao
interface EventJournalDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE) // Append-only, ID auto-generates
    suspend fun appendEvent(event: EventJournalEntity): Long

    @Query("SELECT * FROM event_journal WHERE streamId = :streamId ORDER BY occurredAt ASC")
    suspend fun getStream(streamId: String): List<EventJournalEntity>

    @Query("SELECT * FROM event_journal ORDER BY occurredAt ASC")
    suspend fun getAllEvents(): List<EventJournalEntity>
}
