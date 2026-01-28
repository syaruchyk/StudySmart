package com.studysmart.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.studysmart.features.ingest.pdf.ui.IngestPdfScreen
import com.studysmart.features.ingest.pdf.ui.IngestPdfViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Manual ViewModel instantiation for now
        val ingestPdfViewModel by viewModels<IngestPdfViewModel>(
            factoryProducer = {
                object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        val context = applicationContext
                        val database = com.studysmart.core.data.db.AppDatabase.getInstance(context)
                        // Using the default PdfPipeline inside PdfIngestionUseCase for now
                        val useCase = com.studysmart.features.ingest.pdf.PdfIngestionUseCase(context, database)
                        return IngestPdfViewModel(useCase) as T
                    }
                }
            }
        )

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    IngestPdfScreen(
                        viewModel = ingestPdfViewModel,
                        onIngestSuccess = { quizId ->
                            // TODO: Navigate to Quiz screen
                            // For now, maybe just log or show a toast?
                            // Or reset state if we want to loop.
                        }
                    )
                }
            }
        }
    }
}
