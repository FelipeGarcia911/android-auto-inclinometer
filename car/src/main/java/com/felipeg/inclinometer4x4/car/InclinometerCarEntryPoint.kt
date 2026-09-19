package com.felipeg.inclinometer4x4.car

import com.felipeg.common.domain.repository.OrientationRepository
import com.felipeg.common.domain.usecase.ObserveOrientationUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface InclinometerCarEntryPoint {
    fun orientationRepository(): OrientationRepository
    fun observeOrientationUseCase(): ObserveOrientationUseCase
}
