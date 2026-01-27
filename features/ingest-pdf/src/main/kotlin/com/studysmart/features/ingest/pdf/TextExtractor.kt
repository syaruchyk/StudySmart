package com.studysmart.features.ingest.pdf

import android.net.Uri

interface TextExtractor {
    suspend fun extract(uri: Uri): ExtractionResult
}

data class ExtractionResult(
    val text: String,
    val pages: List<PageContent>
)

data class PageContent(
    val pageNumber: Int,
    val text: String
)
