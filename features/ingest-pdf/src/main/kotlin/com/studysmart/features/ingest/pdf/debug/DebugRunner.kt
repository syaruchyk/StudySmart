package com.studysmart.features.ingest.pdf.debug

import android.content.Context
import android.net.Uri
import com.studysmart.core.data.db.AppDatabase
import com.studysmart.features.ingest.pdf.pipeline.DefaultPdfIngestPipeline
import com.studysmart.features.ingest.pdf.pipeline.OcrProcessor
import com.studysmart.features.ingest.pdf.pipeline.PdfBoxExtractor
import com.studysmart.features.ingest.pdf.usecase.PdfIngestionUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Temporary verification object to ensure all PDF Pipeline components
 * can be wired together manually (proving contracts align).
 */
object DebugRunner {

    fun verifyWiring(context: Context) {
        // 1. Database & DAOs (Manual retrieval, no Hilt)
        val database = AppDatabase.getInstance(context)
        val documentDao = database.documentDao()

        // 2. Extractors
        val pdfBoxExtractor = PdfBoxExtractor(context)
        val ocrExtractor = OcrProcessor(context)

        // 3. Pipeline Coordinator
        val pipeline = DefaultPdfIngestPipeline(
            pdfBoxExtractor = pdfBoxExtractor,
            ocrExtractor = ocrExtractor
        )

        // 4. UseCase (The full assembly)
        val useCase = PdfIngestionUseCase(
            context = context,
            pipeline = pipeline,
            documentDao = documentDao
        )

        // 5. Usage Compilation Check (Simulated)
        // This ensures the invoke operator and return types match what is expected.
        CoroutineScope(Dispatchers.IO).launch {
            val dummyUri = Uri.parse("content://com.studysmart/dummy.pdf")
            try {
                // invoke returns IngestResult
                val result = useCase(dummyUri) 
                println("Wiring Verified. Result ID: ${result.documentId}, Pages: ${result.pageCount}, OCR: ${result.usedOcr}")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
