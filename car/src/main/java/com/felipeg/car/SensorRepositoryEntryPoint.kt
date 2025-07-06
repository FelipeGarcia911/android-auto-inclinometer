package com.felipeg.car

import com.felipeg.common.SensorRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface SensorRepositoryEntryPoint {
    fun sensorRepository(): SensorRepository
}
