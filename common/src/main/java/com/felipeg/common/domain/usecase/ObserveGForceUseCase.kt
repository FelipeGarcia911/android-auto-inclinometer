package com.felipeg.common.domain.usecase

import com.felipeg.common.domain.model.GForceReading
import com.felipeg.common.domain.repository.GForceRepository
import kotlinx.coroutines.flow.Flow

class ObserveGForceUseCase(
    private val repository: GForceRepository
) {
    operator fun invoke(): Flow<GForceReading> = repository.readings
}
