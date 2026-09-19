package com.felipeg.common.domain.usecase

import com.felipeg.common.domain.model.DeviceRotation
import com.felipeg.common.domain.repository.OrientationRepository

class UpdateDeviceRotationUseCase(
    private val repository: OrientationRepository
) {
    operator fun invoke(rotation: DeviceRotation) = repository.updateDeviceRotation(rotation)
}
