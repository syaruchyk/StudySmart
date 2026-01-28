package com.studysmart.features.ingest.pdf

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.studysmart.core.data.db.AppDatabase
import com.studysmart.core.data.db.entities.ChunkEntity
import com.studysmart.core.data.db.entities.DocumentEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.studysmart.core.domain.telemetry.TelemetryClient


class PdfIngestionUseCase(
    private val context: Context,
    private val database: AppDatabase,
    private val telemetry: TelemetryClient,
    private val textExtractor: TextExtractor = PdfPipeline(context)
) {


    suspend fun invoke(uri: Uri): Long = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        telemetry.sendEvent("ingest_started")

        try {
            // 1. Resolve Filename
            val filename = getFileName(uri) ?: uri.lastPathSegment ?: "unknown.pdf"

            // 2. Run Pipeline
            val result = textExtractor.extract(uri)

            // 3. Persist
            val document = DocumentEntity(
                filename = filename,
                uri = uri.toString(),
                importedAt = System.currentTimeMillis(),
                summary = null
            )
            
            val docId = database.documentDao().insertDocument(document)
            
            val chunks = result.pages.map { page ->
                ChunkEntity(
                    documentId = docId,
                    content = page.text,
                    pageNumber = page.pageNumber,
                    startOffset = 0,
                    endOffset = page.text.length
                )
            }
            
            // Batch insert chunks
            database.documentDao().insertChunks(chunks)

            telemetry.sendEvent("ingest_completed", mapOf(
                "duration_ms" to (System.currentTimeMillis() - startTime),
                "pages_count" to result.pages.size,
                "doc_id_hash" to docId
            ))
            
            return@withContext docId
        } catch (e: Exception) {
            telemetry.sendEvent("ingest_failed", mapOf(
                "error_type" to (e::class.simpleName ?: "Unknown")
            ))
            throw e
        }
    }

    private fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                     val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                     if (index >= 0) {
                         result = cursor.getString(index)
                     }
                }
            } catch (e: Exception) {
                // Ignore
            } finally {
                cursor?.close()
            }
        }
        return result
    }
}
