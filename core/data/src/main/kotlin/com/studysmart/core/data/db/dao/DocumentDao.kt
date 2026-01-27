package com.studysmart.core.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.studysmart.core.data.db.entities.ChunkEntity
import com.studysmart.core.data.db.entities.DocumentEntity

@Dao
interface DocumentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: DocumentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChunks(chunks: List<ChunkEntity>)

    @Transaction
    suspend fun insertDocumentWithChunks(document: DocumentEntity, chunks: List<ChunkEntity>) {
        val docId = insertDocument(document)
        val chunksWithId = chunks.map { it.copy(documentId = docId) }
        insertChunks(chunksWithId)
    }

    @Query("SELECT * FROM documents WHERE id = :id")
    suspend fun getDocument(id: Long): DocumentEntity?

    @Query("SELECT * FROM chunks WHERE documentId = :documentId ORDER BY pageNumber ASC, startOffset ASC")
    suspend fun getChunksForDocument(documentId: Long): List<ChunkEntity>
}
