package com.studysmart.core.data.db.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "event_journal",
    indices = [Index(value = ["streamId", "occurredAt"], name = "index_journal_streamId_occurredAt")]
)
data class EventJournalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val streamId: String,
    val eventType: String,
    val payload: String, // JSON
    val occurredAt: Long
)

@Entity(tableName = "session_snapshots")
data class SessionSnapshotEntity(
    @PrimaryKey val sessionId: Long,
    val snapshotData: String, // JSON
    val createdAt: Long
)
