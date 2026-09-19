package com.felipeg.common.domain.model

data class SensorSettings(
    val orientationFilter: OrientationFilterType = OrientationFilterType.LOW_PASS,
    val samplingPeriod: SensorSamplingPeriod = SensorSamplingPeriod.GAME
)

enum class OrientationFilterType {
    LOW_PASS,
    COMPLEMENTARY,
    KALMAN
}

enum class SensorSamplingPeriod {
    FASTEST,
    GAME,
    UI,
    NORMAL
}
