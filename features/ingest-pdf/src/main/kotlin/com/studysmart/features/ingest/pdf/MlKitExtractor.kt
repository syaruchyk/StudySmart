package com.studysmart.features.ingest.pdf

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MlKitExtractor(
    private val context: Context
) : TextExtractor {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    override suspend fun extract(uri: Uri): ExtractionResult = withContext(Dispatchers.IO) {
        val fileDescriptor: ParcelFileDescriptor? = context.contentResolver.openFileDescriptor(uri, "r")
        if (fileDescriptor == null) {
             throw IllegalArgumentException("Cannot open URI for rendering: $uri")
        }

        val renderer = PdfRenderer(fileDescriptor)
        val fullTextBuilder = StringBuilder()
        val pages = mutableListOf<PageContent>()

        try {
            val pageCount = renderer.pageCount
            for (i in 0 until pageCount) {
                 val page = renderer.openPage(i)
                 var bitmap: Bitmap? = null
                 try {
                     val width = page.width
                     val height = page.height
                     
                     // Scale to max width 1200px while maintaining aspect ratio
                     val maxWidth = 1200
                     val finalWidth: Int
                     val finalHeight: Int
                     
                     if (width > maxWidth) {
                         val scale = maxWidth.toFloat() / width
                         finalWidth = maxWidth
                         finalHeight = (height * scale).toInt()
                     } else {
                         finalWidth = width
                         finalHeight = height
                     }

                     bitmap = Bitmap.createBitmap(finalWidth, finalHeight, Bitmap.Config.ARGB_8888)
                     page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

                     val image = InputImage.fromBitmap(bitmap, 0)
                     val mlKitTask = recognizer.process(image)
                     // Blocking await on IO thread is acceptable here
                     val result = Tasks.await(mlKitTask)
                     
                     val pageText = result.text.trim()
                     if (pageText.isNotEmpty()) {
                         fullTextBuilder.append(pageText).append("\n\n")
                         pages.add(PageContent(pageNumber = i + 1, text = pageText))
                     }

                 } finally {
                     bitmap?.recycle()
                     page.close()
                 }
            }
        } finally {
            try {
                renderer.close()
            } catch (e: Exception) {
                // Ignore close errors
            }
            try {
                fileDescriptor.close()
            } catch (e: Exception) {
                 // Ignore close errors
            }
        }

        ExtractionResult(
            text = fullTextBuilder.toString().trim(),
            pages = pages
        )
    }
}
