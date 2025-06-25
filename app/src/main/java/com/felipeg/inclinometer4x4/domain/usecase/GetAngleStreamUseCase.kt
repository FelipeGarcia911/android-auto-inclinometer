package com.felipeg.inclinometer4x4.domain.usecase

import com.felipeg.common.Angle
import com.felipeg.common.SensorRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAngleStreamUseCase @Inject constructor(
    private val repository: SensorRepository
) {
    operator fun invoke(): Flow<Angle> = repository.angleFlow
}
