package com.studysmart.features.practice.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.studysmart.features.practice.ui.viewmodel.PracticeUiState
import com.studysmart.features.practice.ui.viewmodel.PracticeViewModel

@Composable
fun PracticeScreen(
    viewModel: PracticeViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Crossfade(targetState = state.uiState, label = "practice_content") { uiState ->
            when (uiState) {
                is PracticeUiState.Idle -> {
                    // This could be the Start screen
                    PracticeStartContent(
                        onStart = { quizId -> viewModel.startSession(quizId) }
                    )
                }
                is PracticeUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is PracticeUiState.ShowingQuestion -> {
                    PracticeQuestionContent(
                        question = uiState.question,
                        progress = uiState.progress,
                        total = uiState.total,
                        onOptionSelected = { optionId -> 
                            // Time taken calculation could be here or inside ViewModel
                            viewModel.submitAnswer(optionId, 1000L) 
                        }
                    )
                }
                is PracticeUiState.Feedback -> {
                    PracticeFeedbackContent(
                        isCorrect = uiState.isCorrect,
                        correctOptionId = uiState.correctOptionId,
                        explanation = uiState.explanation,
                        onContinue = { viewModel.nextQuestion() }
                    )
                }
                is PracticeUiState.Completed -> {
                    PracticeSummaryContent(
                        session = uiState.session,
                        onFinish = onBack
                    )
                }
                is PracticeUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Error: ${uiState.message}", color = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { viewModel.retry() }) {
                                Text("Reintentar")
                            }
                        }
                    }
                }
            }
        }
    }
}
