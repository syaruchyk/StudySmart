package com.studysmart.features.ingest.pdf.pipeline

/**
 * Internal payload containing the extracted raw text from the pipeline.
 *
 * @property pages Content per page (index 0 = page 1).
 * @property usedOcr Whether OCR was triggered during extraction.
 */
internal data class PipelinePayload(
    val pages: List<String>,
    val usedOcr: Boolean
)
