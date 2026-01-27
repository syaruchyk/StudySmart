package com.studysmart.features.ingest.pdf

import android.content.Context
import android.net.Uri
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader

class PdfBoxExtractor(
    private val context: Context
) : TextExtractor {

    init {
        PDFBoxResourceLoader.init(context)
    }

    override suspend fun extract(uri: Uri): ExtractionResult = withContext(Dispatchers.IO) {
        var inputStream: InputStream? = null
        var document: PDDocument? = null
        try {
            inputStream = context.contentResolver.openInputStream(uri)
                ?: throw IllegalArgumentException("Cannot open URI: $uri")
            
            document = PDDocument.load(inputStream)
            val stripper = PDFTextStripper()
            val fullTextBuilder = StringBuilder()
            val pages = mutableListOf<PageContent>()

            val numberOfPages = document.numberOfPages
            for (pageIndex in 0 until numberOfPages) {
                // PDFTextStripper is 1-based for pages
                stripper.startPage = pageIndex + 1
                stripper.endPage = pageIndex + 1
                
                val pageText = stripper.getText(document).trim()
                if (pageText.isNotEmpty()) {
                    fullTextBuilder.append(pageText).append("\n\n")
                    pages.add(PageContent(pageNumber = pageIndex + 1, text = pageText))
                }
            }

            ExtractionResult(
                text = fullTextBuilder.toString().trim(),
                pages = pages
            )
        } finally {
            document?.close()
            inputStream?.close()
        }
    }
}
