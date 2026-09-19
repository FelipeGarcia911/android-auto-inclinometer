package com.felipeg.common.domain.usecase

import com.felipeg.common.domain.model.Orientation
import com.felipeg.common.domain.model.OrientationReading
import com.felipeg.common.domain.repository.OrientationRepository
import kotlinx.coroutines.flow.Flow

class ObserveOrientationUseCase(
    private val repository: OrientationRepository
) {
    operator fun invoke(): Flow<Orientation> = repository.orientations

    fun readings(): Flow<OrientationReading> = repository.readings
}
