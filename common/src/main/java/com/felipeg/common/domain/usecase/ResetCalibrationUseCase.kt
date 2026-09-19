package com.felipeg.common.domain.usecase

import com.felipeg.common.domain.repository.CalibrationRepository
import com.felipeg.common.domain.repository.GForceRepository

class ResetCalibrationUseCase(
    private val calibrationRepository: CalibrationRepository,
    private val gForceRepository: GForceRepository
) {
    suspend operator fun invoke() {
        calibrationRepository.reset()
        gForceRepository.resetPeak()
    }
}
