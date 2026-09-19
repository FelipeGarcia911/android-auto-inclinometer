package com.felipeg.common.platform.filters

import android.hardware.SensorManager
import com.felipeg.common.domain.model.OrientationFilterType
import com.tracqi.fsensor.sensor.FSensor
import com.tracqi.fsensor.sensor.orientation.KalmanOrientationFSensor
import javax.inject.Inject

class KalmanOrientationFilter @Inject constructor() : OrientationFilter {
    override val type = OrientationFilterType.KALMAN

    override fun create(sensorManager: SensorManager): FSensor =
        KalmanOrientationFSensor(sensorManager)
}
