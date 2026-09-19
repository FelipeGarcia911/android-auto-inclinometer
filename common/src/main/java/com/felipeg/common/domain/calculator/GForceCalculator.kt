package com.felipeg.common.domain.calculator

import com.felipeg.common.domain.model.GForce
import kotlin.math.sqrt

class GForceCalculator {
    fun fromAcceleration(xMetersPerSecondSquared: Float, yMetersPerSecondSquared: Float): GForce =
        GForce(
            x = sanitize(xMetersPerSecondSquared) / STANDARD_GRAVITY,
            y = sanitize(yMetersPerSecondSquared) / STANDARD_GRAVITY
        )

    fun magnitude(gForce: GForce): Float = sqrt(gForce.x * gForce.x + gForce.y * gForce.y)

    fun nextPeak(gForce: GForce, previousPeak: Float): Float =
        maxOf(previousPeak, magnitude(gForce))

    private fun sanitize(value: Float): Float = if (value.isFinite()) value else 0f

    private companion object {
        const val STANDARD_GRAVITY = 9.80665f
    }
}
