package com.studysmart.features.practice.domain

class UpdateSrsUseCase(
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(questionId: Long, isCorrect: Boolean) {
        val currentSrs = sessionRepository.getSrsData(questionId)
        val nextSrs = SrsAlgorithm.calculateNext(currentSrs, isCorrect)
        sessionRepository.updateSrsState(questionId, nextSrs)
    }
}
