package com.felipeg.common.domain.engine

import com.felipeg.common.domain.model.DeviceRotation
import com.felipeg.common.domain.model.Orientation
import com.felipeg.common.domain.model.Quaternion
import kotlin.math.asin
import kotlin.math.atan2

data class VehicleOrientationResult(
    val relativeRotation: Quaternion,
    val rawOrientation: Orientation,
    val calibratedOrientation: Orientation
)

class VehicleOrientationEngine {
    fun calculate(
        current: Quaternion,
        reference: Quaternion?,
        deviceRotation: DeviceRotation
    ): VehicleOrientationResult {
        val normalizedCurrent = current.normalized()
        val relative = ((reference?.normalized() ?: Quaternion.Identity).inverse() * normalizedCurrent)
            .normalized()

        return VehicleOrientationResult(
            relativeRotation = relative,
            rawOrientation = toVehicleOrientation(normalizedCurrent, deviceRotation),
            calibratedOrientation = toVehicleOrientation(relative, deviceRotation)
        )
    }

    private fun toVehicleOrientation(
        quaternion: Quaternion,
        deviceRotation: DeviceRotation
    ): Orientation {
        val q = remapQuaternion(quaternion.normalized(), deviceRotation)
        val rollRadians = atan2(
            2.0 * (q.w * q.x + q.y * q.z),
            1.0 - 2.0 * (q.x * q.x + q.y * q.y)
        )
        val pitchSin = (2.0 * (q.w * q.y - q.z * q.x)).coerceIn(-1.0, 1.0)
        val pitchRadians = asin(pitchSin)
        val azimuthRadians = atan2(
            2.0 * (q.w * q.z + q.x * q.y),
            1.0 - 2.0 * (q.y * q.y + q.z * q.z)
        )

        return Orientation(
            pitch = Math.toDegrees(pitchRadians).toFloat(),
            roll = Math.toDegrees(rollRadians).toFloat(),
            azimuth = Math.toDegrees(azimuthRadians).toFloat()
        )
    }

    private fun remapQuaternion(
        quaternion: Quaternion,
        rotation: DeviceRotation
    ): Quaternion {
        val displayRotationDegrees = when (rotation) {
            DeviceRotation.ROTATION_0 -> 0f
            DeviceRotation.ROTATION_90 -> 90f
            DeviceRotation.ROTATION_180 -> 180f
            DeviceRotation.ROTATION_270 -> 270f
        }
        if (displayRotationDegrees == 0f) return quaternion

        val coordinateRotation = Quaternion.fromEulerDegrees(
            pitch = 0f,
            roll = 0f,
            azimuth = displayRotationDegrees
        )
        return (coordinateRotation.inverse() * quaternion * coordinateRotation).normalized()
    }
}
