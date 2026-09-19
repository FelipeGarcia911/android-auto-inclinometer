package com.felipeg.common.data.repository

import com.felipeg.common.data.source.AccelerationSensorDataSource
import com.felipeg.common.domain.calculator.GForceCalculator
import com.felipeg.common.domain.model.GForceReading
import com.felipeg.common.domain.repository.GForceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultGForceRepository @Inject constructor(
    dataSource: AccelerationSensorDataSource,
    private val calculator: GForceCalculator
) : GForceRepository {
    private var peakMagnitude = 0f

    override val readings: Flow<GForceReading> = dataSource.accelerations.map { acceleration ->
        val current = calculator.fromAcceleration(
            acceleration.xMetersPerSecondSquared,
            acceleration.yMetersPerSecondSquared
        )
        peakMagnitude = calculator.nextPeak(current, peakMagnitude)
        GForceReading(current = current, peakMagnitude = peakMagnitude)
    }

    override fun resetPeak() {
        peakMagnitude = 0f
    }
}
