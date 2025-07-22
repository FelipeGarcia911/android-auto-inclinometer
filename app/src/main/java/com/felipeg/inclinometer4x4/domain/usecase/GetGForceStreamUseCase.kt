package com.felipeg.inclinometer4x4.domain.usecase

import com.felipeg.common.domain.repository.SensorRepository
import com.felipeg.common.model.GForce
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGForceStreamUseCase @Inject constructor(
    private val sensorRepository: SensorRepository
) {
    operator fun invoke(): Flow<GForce> = sensorRepository.gForceFlow
}
