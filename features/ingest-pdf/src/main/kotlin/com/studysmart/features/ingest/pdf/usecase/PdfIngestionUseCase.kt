package com.studysmart.features.ingest.pdf.usecase

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.studysmart.core.data.db.dao.DocumentDao
import com.studysmart.core.data.db.entities.ChunkEntity
import com.studysmart.core.data.db.entities.DocumentEntity
import com.studysmart.features.ingest.pdf.models.IngestResult
import com.studysmart.features.ingest.pdf.pipeline.PdfIngestPipeline
import com.studysmart.features.ingest.pdf.util.Chunker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * UseCase for ingesting a PDF document.
 * Orchestrates:
 * 1. Extraction via [PdfIngestPipeline].
 * 2. Filename resolution.
 * 3. Persistence via [DocumentDao].
 *
 * Note: Only DocumentDao is used as it handles both Documents and Chunks.
 */
class PdfIngestionUseCase internal constructor(
    private val context: Context,
    private val pipeline: PdfIngestPipeline,
    private val documentDao: DocumentDao
) {

    /**
     * Ingests the PDF from [uri].
     *
     * @return [IngestResult] containing the ID of the persisted document.
     */
    suspend operator fun invoke(uri: Uri): IngestResult = withContext(Dispatchers.IO) {
        // 1. Run Pipeline
        val payload = pipeline(uri)

        // 2. Resolve Filename
        val filename = resolveFilename(uri) ?: "Unknown Document"

        // 3. Create Document Entity
        val timestamp = System.currentTimeMillis()
        val documentEntity = DocumentEntity(
            filename = filename,
            uri = uri.toString(),
            importedAt = timestamp,
            summary = null
        )

        // 4. Insert Document & Get ID
        // We do this manually instead of using insertDocumentWithChunks because we need the ID back.
        // DocumentDao already has insertDocument returning Long.
        val docId = documentDao.insertDocument(documentEntity)

        // 5. Chunking & Chunk Entities
        // Chunk each page and create entities
        val chunkEntities = mutableListOf<ChunkEntity>()
        
        payload.pages.forEachIndexed { pageIndex, pageText ->
            val chunks = Chunker.chunk(pageText)
            chunks.forEach { chunkContent ->
                // Note: startOffset/endOffset are not calculated by simple Chunker yet, passing null
                chunkEntities.add(
                    ChunkEntity(
                        documentId = docId,
                        content = chunkContent,
                        pageNumber = pageIndex + 1, // 1-based index
                        startOffset = null, 
                        endOffset = null
                    )
                )
            }
        }

        // 6. Insert Chunks
        if (chunkEntities.isNotEmpty()) {
            documentDao.insertChunks(chunkEntities)
        }

        // 7. Return Result
        IngestResult(
            documentId = docId,
            pageCount = payload.pages.size,
            usedOcr = payload.usedOcr
        )
    }

    private fun resolveFilename(uri: Uri): String? {
        // Basic ContentResolver query to get display name
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (index != -1) {
                        result = it.getString(index)
                    }
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != null && cut != -1) {
                result = result?.substring(cut + 1)
            }
        }
        return result
    }
}
