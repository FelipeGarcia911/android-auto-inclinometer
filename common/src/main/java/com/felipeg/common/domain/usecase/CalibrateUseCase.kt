package com.felipeg.common.domain.usecase

import com.felipeg.common.domain.calculator.CalibrationCalculator
import com.felipeg.common.domain.model.Orientation
import com.felipeg.common.domain.repository.CalibrationRepository
import kotlinx.coroutines.flow.first

class CalibrateUseCase(
    private val repository: CalibrationRepository,
    private val calculator: CalibrationCalculator
) {
    suspend operator fun invoke(current: Orientation) {
        val newOffset = calculator.recalibrate(current, repository.offset.first())
        repository.saveOffset(newOffset)
    }
}
