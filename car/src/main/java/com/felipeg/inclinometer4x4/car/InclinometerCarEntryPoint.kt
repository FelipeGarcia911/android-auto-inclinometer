package com.felipeg.inclinometer4x4.car

import com.felipeg.common.repository.FSensorRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface InclinometerCarEntryPoint {
    fun sensorRepository(): FSensorRepository
}
