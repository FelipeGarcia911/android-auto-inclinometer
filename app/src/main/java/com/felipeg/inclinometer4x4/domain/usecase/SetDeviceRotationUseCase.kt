package com.felipeg.inclinometer4x4.domain.usecase

import com.felipeg.common.SensorRepository
import javax.inject.Inject

class SetDeviceRotationUseCase @Inject constructor(
    private val repository: SensorRepository
) {
    fun execute(rotation: Int) = repository.updateDeviceRotation(rotation)
}
