package com.felipeg.common.data.mapper

import com.felipeg.common.data.model.RawOrientation
import com.felipeg.common.domain.model.Orientation
import kotlin.math.PI

class OrientationMapper {
    fun map(raw: RawOrientation): Orientation = Orientation(
        pitch = pitchToDegrees(raw.pitchRadians),
        roll = radiansToDegrees(raw.rollRadians),
        azimuth = radiansToDegrees(raw.azimuthRadians)
    )

    fun radiansToDegrees(radians: Float): Float =
        if (radians.isFinite()) Math.toDegrees(radians.toDouble()).toFloat() else 0f

    private fun pitchToDegrees(pitch: Float): Float {
        if (!pitch.isFinite()) return 0f
        val sign = if (pitch > 0f) -1f else 1f
        return -radiansToDegrees(pitch + sign * (PI / 2).toFloat())
    }
}
