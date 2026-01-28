package com.studysmart.features.ingest.pdf.pipeline

import android.content.Context
import android.net.Uri
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * Implementation of [PdfTextExtractor] using PdfBox-Android.
 * prioritized for high-fidelity text extraction.
 */
internal class PdfBoxExtractor(
    private val context: Context
) : PdfTextExtractor {

    override suspend fun extract(uri: Uri): List<String> = withContext(Dispatchers.IO) {
        val extractedPages = mutableListOf<String>()

        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                // Load the document using PdfBox.
                // Note: PDDocument.load(InputStream) is generally supported in PdfBox-Android
                PDDocument.load(inputStream).use { document ->
                    val stripper = PDFTextStripper()

                    // Iterate through valid pages (1-based index for PdfBox usually, but getNumberOfPages is 0-based count)
                    for (pageIndex in 0 until document.numberOfPages) {
                        try {
                            // Set the page range for the stripper (1-based)
                            stripper.startPage = pageIndex + 1
                            stripper.endPage = pageIndex + 1
                            
                            val text = stripper.getText(document)
                            extractedPages.add(text)
                        } catch (e: Exception) {
                            // Log error if needed, but per requirements: add empty string and continue
                            extractedPages.add("") 
                        }
                    }
                }
            } ?: run {
                 // returns empty list if stream cannot be opened? Or should it throw?
                 // Requirements say "return List<String>", usually assuming valid PDF.
                 // If stream is null, we can return empty or throw IOException.
                 // Let's safe-guard with empty list logic or single failure.
                 throw IOException("Cannot open input stream for URI: $uri")
            }
        } catch (e: Exception) {
            // General failure (e.g. not a valid PDF or permission denied)
            // Ideally we might want to let this bubble up or return empty.
            // For robust pipeline, bubbling up usually better for the top level to handle, 
            // but requirements say "Si una página falla... no crashear todo".
            // If the *document* load fails, we probably can't do anything.
            // Rethrowing generic issues is often safer for the pipeline to decide to fail hard.
            e.printStackTrace()
            // If we extracted partial pages, return them? Or fail? 
            // Let's assume document load failure is fatal for this extractor.
            // But let's verify if we should just return empty to allow fallback?
            // "Extrae texto con PdfBox como primary."
            // If PdfBox crashes hard, maybe we should let the pipeline catch it and try OCR?
            // For now, let's just return what we have (empty) so heuristic might trigger OCR if pages are empty.
        }

        extractedPages
    }
}
