package com.felipeg.common.di

import android.content.Context
import android.hardware.SensorManager
import com.felipeg.common.data.mapper.OrientationMapper
import com.felipeg.common.domain.calculator.CalibrationCalculator
import com.felipeg.common.domain.calculator.GForceCalculator
import com.felipeg.common.domain.calculator.RotationTransformer
import com.felipeg.common.domain.repository.CalibrationRepository
import com.felipeg.common.domain.repository.GForceRepository
import com.felipeg.common.domain.repository.OrientationRepository
import com.felipeg.common.domain.usecase.CalibrateUseCase
import com.felipeg.common.domain.usecase.ObserveGForceUseCase
import com.felipeg.common.domain.usecase.ObserveOrientationUseCase
import com.felipeg.common.domain.usecase.ResetCalibrationUseCase
import com.felipeg.common.domain.usecase.UpdateDeviceRotationUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object SensorProvidesModule {
    @Provides
    fun provideSensorManager(@ApplicationContext context: Context): SensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    @Provides
    fun provideOrientationMapper() = OrientationMapper()

    @Provides
    fun provideRotationTransformer() = RotationTransformer()

    @Provides
    fun provideCalibrationCalculator() = CalibrationCalculator()

    @Provides
    fun provideGForceCalculator() = GForceCalculator()

    @Provides
    fun provideObserveOrientationUseCase(repository: OrientationRepository) =
        ObserveOrientationUseCase(repository)

    @Provides
    fun provideObserveGForceUseCase(repository: GForceRepository) = ObserveGForceUseCase(repository)

    @Provides
    fun provideCalibrateUseCase(
        repository: CalibrationRepository,
        calculator: CalibrationCalculator
    ) = CalibrateUseCase(repository, calculator)

    @Provides
    fun provideResetCalibrationUseCase(
        calibrationRepository: CalibrationRepository,
        gForceRepository: GForceRepository
    ) = ResetCalibrationUseCase(calibrationRepository, gForceRepository)

    @Provides
    fun provideUpdateDeviceRotationUseCase(repository: OrientationRepository) =
        UpdateDeviceRotationUseCase(repository)
}
