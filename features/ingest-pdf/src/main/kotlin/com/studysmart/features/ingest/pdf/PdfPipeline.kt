package com.studysmart.features.ingest.pdf

import android.content.Context
import android.net.Uri

class PdfPipeline(
    private val context: Context
) : TextExtractor {

    private val pdfBoxExtractor = PdfBoxExtractor(context)
    private val mlKitExtractor = MlKitExtractor(context)

    override suspend fun extract(uri: Uri): ExtractionResult {
        // 1. Try Direct Extraction (PdfBox)
        val directResult = try {
            pdfBoxExtractor.extract(uri)
        } catch (e: Exception) {
            e.printStackTrace()
            // If PdfBox fails outright, fallback immediately
            ExtractionResult("", emptyList())
        }

        // 2. Validate Result
        if (isValidExtraction(directResult)) {
            return directResult
        }

        // 3. Fallback to OCR (MLKit)
        return mlKitExtractor.extract(uri)
    }

    private fun isValidExtraction(result: ExtractionResult): Boolean {
        if (result.pages.isEmpty()) return false
        
        // Custom Heuristic: Average characters per page
        // "Sparse" text often means images with hidden text or bad encoding
        val totalChars = result.text.length
        val avgCharsPerPage = totalChars / result.pages.size
        
        return avgCharsPerPage > 50 
    }
}
