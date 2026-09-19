package com.felipeg.common.platform.filters

import android.hardware.SensorManager
import com.felipeg.common.domain.model.OrientationFilterType
import com.tracqi.fsensor.sensor.FSensor

interface OrientationFilter {
    val type: OrientationFilterType

    fun create(sensorManager: SensorManager): FSensor
}
