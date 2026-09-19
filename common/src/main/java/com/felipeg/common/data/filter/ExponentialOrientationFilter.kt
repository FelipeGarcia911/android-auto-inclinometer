package com.felipeg.common.data.filter

import com.felipeg.common.domain.filter.OrientationFilter
import com.felipeg.common.domain.model.Orientation

class ExponentialOrientationFilter(
    private val alpha: Float
) : OrientationFilter {
    private var previous: Orientation? = null

    init {
        require(alpha in 0f..1f) { "Alpha must be between 0 and 1" }
    }

    override fun filter(orientation: Orientation): Orientation {
        val last = previous
        val filtered = if (last == null) orientation else Orientation(
            pitch = smooth(last.pitch, orientation.pitch),
            roll = smooth(last.roll, orientation.roll),
            azimuth = smooth(last.azimuth, orientation.azimuth)
        )
        previous = filtered
        return filtered
    }

    override fun reset() {
        previous = null
    }

    private fun smooth(previous: Float, current: Float): Float =
        alpha * current + (1f - alpha) * previous
}
