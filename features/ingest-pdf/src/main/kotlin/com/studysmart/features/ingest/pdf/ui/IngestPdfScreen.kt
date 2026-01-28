package com.studysmart.features.ingest.pdf.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun IngestPdfScreen(
    viewModel: IngestPdfViewModel, // In real app, use hiltViewModel()
    onIngestSuccess: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is IngestUiState.Idle -> {
                    FileSelector(onFileSelected = { uri ->
                        viewModel.onFileSelected(uri)
                    })
                }
                is IngestUiState.Ingesting -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "Ingesting PDF...")
                    }
                }
                is IngestUiState.GeneratingQuiz -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "Generating Quiz...")
                        Text(text = "This might take a moment.", style = MaterialTheme.typography.bodySmall)
                    }
                }
                is IngestUiState.Success -> {
                    // Trigger navigation or show success
                    // For now, simple text + button to proceed
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Ingestion Complete!", style = MaterialTheme.typography.headlineMedium)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { onIngestSuccess(state.quizId) }) {
                            Text("Go to Quiz")
                        }
                    }
                }
                is IngestUiState.Error -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Error: ${state.message}", 
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.onRetry() }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FileSelector(onFileSelected: (Uri) -> Unit) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            uri?.let { onFileSelected(it) }
        }
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Select a PDF to generate a Quiz",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { 
            launcher.launch(arrayOf("application/pdf")) 
        }) {
            Text("Pick PDF File")
        }
    }
}
