package com.felipeg.inclinometer4x4.domain.usecase

import com.felipeg.inclinometer4x4.domain.repository.FSensorRepository
import javax.inject.Inject

class SetDeviceRotationUseCase @Inject constructor(
    private val fSensorRepository: FSensorRepository
) {
    fun execute(rotation: Int) {
        fSensorRepository.setDeviceRotation(rotation)
    }
}
