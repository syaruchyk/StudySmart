package com.studysmart.features.ingest.pdf.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studysmart.core.domain.Result
import com.studysmart.features.ingest.pdf.PdfIngestionUseCase
import com.studysmart.features.ingest.pdf.pipeline.PdfIngestPipeline
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class IngestUiState {
    object Idle : IngestUiState()
    object Ingesting : IngestUiState()
    data class Success(val quizId: String) : IngestUiState() // Assuming pipeline result could give ID, or generic success
    data class Error(val message: String) : IngestUiState()
}

class IngestPdfViewModel(
    private val ingestPdfUseCase: PdfIngestionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<IngestUiState>(IngestUiState.Idle)
    val uiState: StateFlow<IngestUiState> = _uiState.asStateFlow()

    fun onFileSelected(uri: Uri) {
        viewModelScope.launch {
            _uiState.value = IngestUiState.Ingesting
            
            try {
                // Execute actual ingestion
                val docId = ingestPdfUseCase.invoke(uri)
                _uiState.value = IngestUiState.Success(docId.toString())
            } catch (e: Exception) {
                _uiState.value = IngestUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    fun onRetry() {
        _uiState.value = IngestUiState.Idle
    }
}
