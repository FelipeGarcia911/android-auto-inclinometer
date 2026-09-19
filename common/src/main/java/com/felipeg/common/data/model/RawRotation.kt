package com.felipeg.common.data.model

import com.felipeg.common.domain.model.Quaternion
import com.felipeg.common.domain.model.RotationSensorType

data class RawRotation(
    val quaternion: Quaternion,
    val sensorType: RotationSensorType,
    val samplingRateHz: Float
)
