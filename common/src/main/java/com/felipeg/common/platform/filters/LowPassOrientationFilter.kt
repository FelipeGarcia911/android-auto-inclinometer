package com.felipeg.common.platform.filters

import android.hardware.SensorManager
import com.felipeg.common.domain.model.OrientationFilterType
import com.tracqi.fsensor.sensor.FSensor
import com.tracqi.fsensor.sensor.orientation.LowPassOrientationFSensor
import javax.inject.Inject

class LowPassOrientationFilter @Inject constructor() : OrientationFilter {
    override val type = OrientationFilterType.LOW_PASS

    override fun create(sensorManager: SensorManager): FSensor =
        LowPassOrientationFSensor(sensorManager)
}
