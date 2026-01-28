package com.studysmart.features.ingest.pdf.pipeline

import android.content.Context
import android.net.Uri
import com.studysmart.features.ingest.pdf.util.TextDensity

/**
 * Coordinator for the PDF ingestion pipeline.
 * Orchestrates [PdfBoxExtractor] and [OcrProcessor] based on [TextDensity] heuristics.
 */
internal class DefaultPdfIngestPipeline(
    private val pdfBoxExtractor: PdfTextExtractor,
    private val ocrExtractor: PdfTextExtractor
) : PdfIngestPipeline {

    override suspend fun invoke(uri: Uri): PipelinePayload {
        // 1. Primary Extraction (PdfBox)
        val pdfBoxPages = pdfBoxExtractor.extract(uri)

        // 2. Evaluate Heuristics
        val useOcr = TextDensity.shouldUseOcr(pdfBoxPages)

        return if (useOcr) {
            // 3. Fallback to OCR
            val ocrPages = ocrExtractor.extract(uri)
            // If OCR returns empty but PdfBox had *something* (even if low density), maybe fallback to PdfBox?
            // Requirement says: "if shouldUseOcr -> pages = ocrExtractor.extract". We stick to strict plan.
            // Edge case: OCR completely fails (empty list).
            if (ocrPages.isEmpty() && pdfBoxPages.isNotEmpty()) {
                // If OCR fails completely, better to return what PdfBox found than nothing?
                // User requirement didn't specify this fallback-back, so we return OCR result as requested.
                // But for robustness, if OCR is empty, we return empty.
                PipelinePayload(ocrPages, usedOcr = true)
            } else {
                PipelinePayload(ocrPages, usedOcr = true)
            }
        } else {
            PipelinePayload(pdfBoxPages, usedOcr = false)
        }
    }
}
