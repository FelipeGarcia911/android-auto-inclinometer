package com.felipeg.common.di

import com.felipeg.common.data.repository.DefaultGForceRepository
import com.felipeg.common.data.repository.DefaultOrientationRepository
import com.felipeg.common.data.source.AccelerationSensorDataSource
import com.felipeg.common.data.source.OrientationSensorDataSource
import com.felipeg.common.domain.repository.CalibrationRepository
import com.felipeg.common.domain.repository.GForceRepository
import com.felipeg.common.domain.repository.OrientationRepository
import com.felipeg.common.domain.repository.SettingsRepository
import com.felipeg.common.platform.preferences.DataStoreCalibrationRepository
import com.felipeg.common.platform.preferences.DataStoreSettingsRepository
import com.felipeg.common.platform.sensors.AndroidAccelerationSensorDataSource
import com.felipeg.common.platform.sensors.AndroidRotationVectorDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SensorBindingsModule {
    @Binds
    @Singleton
    abstract fun bindOrientationRepository(impl: DefaultOrientationRepository): OrientationRepository

    @Binds
    @Singleton
    abstract fun bindGForceRepository(impl: DefaultGForceRepository): GForceRepository

    @Binds
    @Singleton
    abstract fun bindOrientationSensorDataSource(
        impl: AndroidRotationVectorDataSource
    ): OrientationSensorDataSource

    @Binds
    @Singleton
    abstract fun bindAccelerationSensorDataSource(
        impl: AndroidAccelerationSensorDataSource
    ): AccelerationSensorDataSource

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: DataStoreSettingsRepository): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindCalibrationRepository(
        impl: DataStoreCalibrationRepository
    ): CalibrationRepository
}
