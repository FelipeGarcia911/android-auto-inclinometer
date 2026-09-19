package com.felipeg.common.domain.model

data class GForce(
    val x: Float,
    val y: Float
) {
    companion object {
        val Zero = GForce(0f, 0f)
    }
}

data class GForceReading(
    val current: GForce = GForce.Zero,
    val peakMagnitude: Float = 0f
)
