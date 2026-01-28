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

import com.studysmart.features.generatequiz.domain.GenerateQuizUseCase

sealed class IngestUiState {
    object Idle : IngestUiState()
    object Ingesting : IngestUiState()
    object GeneratingQuiz : IngestUiState()
    data class Success(val quizId: String) : IngestUiState()
    data class Error(val message: String) : IngestUiState()
}

class IngestPdfViewModel(
    private val ingestPdfUseCase: PdfIngestionUseCase,
    private val generateQuizUseCase: GenerateQuizUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<IngestUiState>(IngestUiState.Idle)
    val uiState: StateFlow<IngestUiState> = _uiState.asStateFlow()

    fun onFileSelected(uri: Uri) {
        viewModelScope.launch {
            _uiState.value = IngestUiState.Ingesting
            
            try {
                // 1. Ingest PDF
                val docId = ingestPdfUseCase.invoke(uri)
                
                // 2. Generate Quiz
                _uiState.value = IngestUiState.GeneratingQuiz
                
                // Hardcoded API Key for now (Prototype). In prod, use BuildConfig/Secrets.
                // Or better, pass it in via UseCase or some Config provider.
                // Assuming we use a placeholder or system property for this demo.
                val apiKey = com.studysmart.core.domain.BuildConfig.GEMINI_API_KEY 
                    ?: throw IllegalStateException("Gemini API Key not found in BuildConfig")
                    
                val quizId = generateQuizUseCase.invoke(docId, apiKey)
                
                _uiState.value = IngestUiState.Success(quizId.toString())
            } catch (e: Exception) {
                _uiState.value = IngestUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    fun onRetry() {
        _uiState.value = IngestUiState.Idle
    }
}
