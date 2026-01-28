package com.studysmart.features.ingest.pdf.pipeline

import android.net.Uri

/**
 * Internal interface for extracting raw text from a PDF source.
 */
internal interface PdfTextExtractor {
    /**
     * Extracts text from the PDF at [uri].
     *
     * @param uri The URI of the PDF.
     * @return A list of strings, where each string represents the content of a page.
     *         Index 0 corresponds to Page 1.
     */
    suspend fun extract(uri: Uri): List<String>
}
