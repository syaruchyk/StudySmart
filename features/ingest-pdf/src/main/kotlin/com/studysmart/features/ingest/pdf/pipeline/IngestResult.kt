package com.studysmart.features.ingest.pdf.pipeline

/**
 * Result of a PDF ingestion process.
 *
 * @property documentId The ID of the persisted document in the database.
 * @property pageCount Total number of pages processed.
 * @property usedOcr Whether OCR was utilized during extraction.
 */
data class IngestResult(
    val documentId: Long,
    val pageCount: Int,
    val usedOcr: Boolean
)
