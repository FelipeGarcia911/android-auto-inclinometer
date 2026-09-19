package com.felipeg.common.domain.model

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class Quaternion(
    val x: Float,
    val y: Float,
    val z: Float,
    val w: Float
) {
    val magnitude: Float
        get() = sqrt(x * x + y * y + z * z + w * w)

    fun normalized(): Quaternion {
        val norm = magnitude
        return if (!norm.isFinite() || norm <= NORMALIZATION_EPSILON) Identity else Quaternion(
            x = x / norm,
            y = y / norm,
            z = z / norm,
            w = w / norm
        )
    }

    fun conjugate(): Quaternion = Quaternion(-x, -y, -z, w)

    fun inverse(): Quaternion {
        val normSquared = x * x + y * y + z * z + w * w
        if (!normSquared.isFinite() || normSquared <= NORMALIZATION_EPSILON) return Identity
        val conjugate = conjugate()
        return Quaternion(
            x = conjugate.x / normSquared,
            y = conjugate.y / normSquared,
            z = conjugate.z / normSquared,
            w = conjugate.w / normSquared
        )
    }

    operator fun times(other: Quaternion): Quaternion = Quaternion(
        x = w * other.x + x * other.w + y * other.z - z * other.y,
        y = w * other.y - x * other.z + y * other.w + z * other.x,
        z = w * other.z + x * other.y - y * other.x + z * other.w,
        w = w * other.w - x * other.x - y * other.y - z * other.z
    )

    companion object {
        val Identity = Quaternion(0f, 0f, 0f, 1f)

        fun fromEulerDegrees(pitch: Float, roll: Float, azimuth: Float = 0f): Quaternion {
            val halfRoll = Math.toRadians(roll.toDouble()) / 2.0
            val halfPitch = Math.toRadians(pitch.toDouble()) / 2.0
            val halfYaw = Math.toRadians(azimuth.toDouble()) / 2.0
            val cr = cos(halfRoll)
            val sr = sin(halfRoll)
            val cp = cos(halfPitch)
            val sp = sin(halfPitch)
            val cy = cos(halfYaw)
            val sy = sin(halfYaw)

            return Quaternion(
                x = (sr * cp * cy - cr * sp * sy).toFloat(),
                y = (cr * sp * cy + sr * cp * sy).toFloat(),
                z = (cr * cp * sy - sr * sp * cy).toFloat(),
                w = (cr * cp * cy + sr * sp * sy).toFloat()
            ).normalized()
        }

        private const val NORMALIZATION_EPSILON = 1e-8f
    }
}
