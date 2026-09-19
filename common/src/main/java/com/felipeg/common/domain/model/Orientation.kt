package com.felipeg.common.domain.model

data class Orientation(
    val pitch: Float,
    val roll: Float,
    val azimuth: Float = 0f
) {
    companion object {
        val Zero = Orientation(0f, 0f, 0f)
    }
}
