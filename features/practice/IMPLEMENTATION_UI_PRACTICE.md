UI Practice Plan:
- Single ViewModel controlling states: Start, Question, Feedback, Summary
- Screens: PracticeStart, PracticeQuestion, PracticeFeedback, PracticeSummary (Jetpack Compose)
- Connect ViewModel to existing UseCases: StartSessionUseCase, GetNextQuestionUseCase, AnswerQuestionUseCase, GetSessionSummaryUseCase
- StateFlow-based UI state; one State data class
- Error handling and retry flows
- Tests: ViewModel unit tests (mock repository/usecases)
