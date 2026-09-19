package com.felipeg.common.domain.model

data class OrientationReading(
    val sensorType: RotationSensorType,
    val samplingRateHz: Float,
    val currentRotation: Quaternion,
    val referenceRotation: Quaternion?,
    val relativeRotation: Quaternion,
    val rawOrientation: Orientation,
    val calibratedOrientation: Orientation,
    val filteredOrientation: Orientation,
    val deviceRotation: DeviceRotation
)
