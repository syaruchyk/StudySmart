package com.studysmart.features.ingest.pdf.pipeline

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Implementation of [PdfTextExtractor] using ML Kit Text Recognition.
 * Acts as a fallback for image-heavy or scanned PDFs.
 */
internal class OcrProcessor(
    private val context: Context
) : PdfTextExtractor {

    override suspend fun extract(uri: Uri): List<String> = withContext(Dispatchers.IO) {
        val extractedPages = mutableListOf<String>()
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        try {
            // openFileDescriptor is required for PdfRenderer
            context.contentResolver.openFileDescriptor(uri, "r")?.use { pfd ->
                PdfRenderer(pfd).use { renderer ->
                    for (pageIndex in 0 until renderer.pageCount) {
                        var page: PdfRenderer.Page? = null
                        var bitmap: Bitmap? = null
                        try {
                            page = renderer.openPage(pageIndex)
                            
                            // Scale dimensions to manage memory but ensure quality (cap width ~1500px?)
                            // Using standard A4 ratio roughly. 
                            // Requirements: "renderiza cada página a Bitmap"
                            // Let's use actual density (72dpi is default usually).
                            // For OCR, 2.0x scale (144dpi) is often good.
                            val width = page.width * 2
                            val height = page.height * 2
                            
                            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

                            val image = InputImage.fromBitmap(bitmap, 0)
                            val text = processImage(recognizer, image)
                            extractedPages.add(text)

                        } catch (e: Exception) {
                            e.printStackTrace()
                            extractedPages.add("")
                        } finally {
                            bitmap?.recycle()
                            page?.close()
                        }
                    }
                }
            } ?: throw IOException("Cannot open file descriptor for URI: $uri")
        } catch (e: Exception) {
            e.printStackTrace()
             // Just like PdfBox, return what we have (empty list = failed)
        } finally {
            // Lifecycle of recognizer? It's closeable but usually single instance in client is fine.
            // official docs say "You can close...". 
            recognizer.close()
        }

        extractedPages
    }

    private suspend fun processImage(
        recognizer: com.google.mlkit.vision.text.TextRecognizer,
        image: InputImage
    ): String = suspendCancellableCoroutine { continuation ->
        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                continuation.resume(visionText.text)
            }
            .addOnFailureListener { e ->
                // Per requirements: "devuelve "" en páginas fallidas" (caught in loop above, but here we can resume with empty)
                // However, the loop catch block handles exceptions. ResumeWithException propagates it.
                // Let's resume with empty string to be safe and avoid throwing inside the async task if we want "soft fail".
                // But generally listener failure = exception.
                continuation.resumeWithException(e)
            }
    }
}
