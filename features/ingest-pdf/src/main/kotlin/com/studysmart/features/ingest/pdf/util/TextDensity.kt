package com.studysmart.features.ingest.pdf.util

/**
 * Heuristics for determining text density and OCR requirements.
 */
object TextDensity {

    /**
     * Calculates if a text block has low density of meaningful content.
     * Heuristic: Count alphanumeric characters. If < 50, consider it low density (likely Scanned/Image).
     *
     * @param text The raw text of a page.
     * @return True if density is below threshold.
     */
    fun isLowDensity(text: String): Boolean {
        // Normalize whitespace and remove common noise if needed, but simple filter is requested.
        val alphanumericCount = text.count { it.isLetterOrDigit() }
        return alphanumericCount < 50
    }

    /**
     * Decides if OCR should be used based on the extracted pages.
     * Rule: If > 30% of pages are "low density", we assume the PDF requires OCR.
     *
     * @param pages List of page texts.
     * @return True if OCR fallback is recommended.
     */
    fun shouldUseOcr(pages: List<String>): Boolean {
        if (pages.isEmpty()) return true // No text extracted? Try OCR.

        val totalPages = pages.size
        val lowDensityPages = pages.count { isLowDensity(it) }

        val lowDensityRatio = lowDensityPages.toDouble() / totalPages.toDouble()
        return lowDensityRatio > 0.3
    }
}
