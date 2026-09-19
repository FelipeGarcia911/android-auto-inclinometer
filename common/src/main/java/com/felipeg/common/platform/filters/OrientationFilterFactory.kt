package com.felipeg.common.platform.filters

import android.hardware.SensorManager
import com.felipeg.common.domain.model.OrientationFilterType
import com.tracqi.fsensor.sensor.FSensor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrientationFilterFactory @Inject constructor(
    lowPass: LowPassOrientationFilter,
    complementary: ComplementaryOrientationFilter,
    kalman: KalmanOrientationFilter
) {
    private val filters = listOf(lowPass, complementary, kalman).associateBy(OrientationFilter::type)

    fun create(type: OrientationFilterType, sensorManager: SensorManager): FSensor =
        requireNotNull(filters[type]) { "Unsupported orientation filter: $type" }
            .create(sensorManager)
}
