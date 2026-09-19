package com.felipeg.common.domain.usecase

import com.felipeg.common.domain.model.Quaternion
import com.felipeg.common.domain.repository.CalibrationRepository

class CalibrateUseCase(
    private val repository: CalibrationRepository
) {
    suspend operator fun invoke(current: Quaternion) {
        repository.saveReference(current.normalized())
    }
}
