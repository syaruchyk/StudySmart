package com.studysmart.features.ingest.pdf.pipeline

import android.net.Uri

/**
 * Internal contract for the PDF Ingestion Pipeline.
 * Orchestrates the extraction (text/OCR) and returns raw text data.
 */
internal interface PdfIngestPipeline {
    /**
     * Ingests a PDF from the given [uri].
     *
     * @param uri The URI of the PDF content.
     * @return [PipelinePayload] containing extracted text and metadata.
     */
    suspend operator fun invoke(uri: Uri): PipelinePayload
}
