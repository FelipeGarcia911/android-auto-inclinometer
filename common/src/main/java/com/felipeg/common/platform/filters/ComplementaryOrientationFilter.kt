package com.felipeg.common.platform.filters

import android.hardware.SensorManager
import com.felipeg.common.domain.model.OrientationFilterType
import com.tracqi.fsensor.sensor.FSensor
import com.tracqi.fsensor.sensor.orientation.ComplementaryOrientationFSensor
import javax.inject.Inject

class ComplementaryOrientationFilter @Inject constructor() : OrientationFilter {
    override val type = OrientationFilterType.COMPLEMENTARY

    override fun create(sensorManager: SensorManager): FSensor =
        ComplementaryOrientationFSensor(sensorManager)
}
