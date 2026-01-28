package com.studysmart.features.ingest.pdf.util

/**
 * Utility for splitting text into smaller chunks.
 */
object Chunker {
    /**
     * Splits a meaningful block of text (e.g., a page) into smaller chunks.
     * Currenlty strict pass-through as detailed logic wasn't requested in strict step.
     *
     * @param text The text to chunk.
     * @return List of text chunks.
     */
    fun chunk(text: String): List<String> {
        // TODO: Implement smart chunking (overlapping windows, etc.)
        // For now, return the whole page as one chunk to satisfy compilation/logic flow
        return listOf(text)
    }
}
