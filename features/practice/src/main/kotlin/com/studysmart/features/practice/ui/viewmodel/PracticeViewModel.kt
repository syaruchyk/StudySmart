package com.studysmart.features.practice.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studysmart.features.practice.domain.*
import com.studysmart.core.domain.telemetry.TelemetryClient
import kotlinx.coroutines.CoroutineExceptionHandler

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface PracticeUiState {
    object Idle : PracticeUiState
    object Loading : PracticeUiState
    data class ShowingQuestion(
        val question: SessionQuestion, 
        val progress: Int, 
        val total: Int
    ) : PracticeUiState
    data class Feedback(
        val isCorrect: Boolean, 
        val correctOptionId: Long,
        val explanation: String?
    ) : PracticeUiState
    data class Completed(val session: PracticeSession) : PracticeUiState
    data class Error(val message: String) : PracticeUiState
}

data class PracticeViewState(
    val uiState: PracticeUiState = PracticeUiState.Idle,
    val currentSession: PracticeSession? = null
)

class PracticeViewModel(
    private val startSessionUseCase: StartSessionUseCase,
    private val getNextQuestionUseCase: GetNextQuestionUseCase,
    private val answerQuestionUseCase: AnswerQuestionUseCase,
    private val getSessionSummaryUseCase: GetSessionSummaryUseCase,
    private val telemetry: TelemetryClient
) : ViewModel() {


    private val _state = MutableStateFlow(PracticeViewState())
    val state: StateFlow<PracticeViewState> = _state.asStateFlow()

    private val handler = CoroutineExceptionHandler { _, throwable ->
        viewModelScope.launch(Dispatchers.Main) {
            _state.value = _state.value.copy(
                uiState = PracticeUiState.Error(throwable.message ?: "Error inesperado")
            )
        }
    }

    fun startSession(quizId: Long) {
        viewModelScope.launch(handler + Dispatchers.IO) {
            _state.value = _state.value.copy(uiState = PracticeUiState.Loading)
            val session = startSessionUseCase(quizId)
            
            telemetry.sendEvent("practice_session_started", mapOf(
                "quiz_id_hash" to quizId
            ))

            val nextState = determineNextState(session)
            if (nextState is PracticeUiState.ShowingQuestion) {
                trackQuestionShown(nextState.question)
            }

            _state.value = _state.value.copy(
                currentSession = session,
                uiState = nextState
            )
        }
    }


    fun submitAnswer(optionId: Long, timeTakenMs: Long) {
        val session = _state.value.currentSession ?: return
        val currentQuestion = session.currentQuestion ?: return
        
        viewModelScope.launch(handler + Dispatchers.IO) {
            _state.value = _state.value.copy(uiState = PracticeUiState.Loading)
            
            // 1. Process answer
            val updatedSession = answerQuestionUseCase(session, optionId, timeTakenMs)
            
            // 2. Determine feedback data
            val correctOption = currentQuestion.options.find { it.isCorrect }
            val isCorrect = currentQuestion.options.find { it.id == optionId }?.isCorrect == true
            
            telemetry.sendEvent("question_answered", mapOf(
                "is_correct" to isCorrect,
                "duration_ms" to timeTakenMs
            ))

            _state.value = _state.value.copy(
                currentSession = updatedSession,
                uiState = PracticeUiState.Feedback(
                    isCorrect = isCorrect,
                    correctOptionId = correctOption?.id ?: -1L,
                    explanation = currentQuestion.explanation
                )
            )
        }
    }


    fun nextQuestion() {
        val session = _state.value.currentSession ?: return
        
        viewModelScope.launch(handler + Dispatchers.IO) {
            _state.value = _state.value.copy(uiState = PracticeUiState.Loading)
            
            val updatedSession = getNextQuestionUseCase(session)
            val nextState = determineNextState(updatedSession)
            
            if (nextState is PracticeUiState.ShowingQuestion) {
                trackQuestionShown(nextState.question)
            } else if (nextState is PracticeUiState.Completed) {
                telemetry.sendEvent("practice_session_completed", mapOf(
                    "correct_answers" to updatedSession.stats.correctAnswers,
                    "total_answered" to updatedSession.stats.totalQuestionsAnswered
                ))
            }

            _state.value = _state.value.copy(
                currentSession = updatedSession,
                uiState = nextState
            )
        }
    }

    private fun trackQuestionShown(question: SessionQuestion) {
        telemetry.sendEvent("question_shown", mapOf(
            "question_id_hash" to question.id
        ))
    }


    private fun determineNextState(session: PracticeSession): PracticeUiState {
        return when (session.state) {
            SessionState.COMPLETED -> PracticeUiState.Completed(session)
            SessionState.QUESTION -> {
                val next = session.currentQuestion
                if (next != null) {
                    val progress = session.stats.totalQuestionsAnswered + 1
                    // Note: We don't have 'totalQuestions' in domain session yet, 
                    // using progress as total for now or 0 if unknown.
                    PracticeUiState.ShowingQuestion(next, progress, 0) 
                } else {
                    PracticeUiState.Error("No hay más preguntas")
                }
            }
            else -> PracticeUiState.Idle
        }
    }

    fun retry() {
        val session = _state.value.currentSession
        if (session != null) {
            nextQuestion()
        } else {
            _state.value = _state.value.copy(uiState = PracticeUiState.Idle)
        }
    }
}
