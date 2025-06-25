package com.felipeg.inclinometer4x4.domain.usecase

import com.felipeg.common.Angle
import com.felipeg.common.SensorRepository
import javax.inject.Inject

class CalibrateZeroUseCase @Inject constructor(
    private val repository: SensorRepository
) {
    fun execute(angle: Angle) = repository.calibrateZero()
}
