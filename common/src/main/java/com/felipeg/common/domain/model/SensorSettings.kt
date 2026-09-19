package com.felipeg.common.domain.model

data class SensorSettings(
    val orientationFilter: OrientationFilterPreset = OrientationFilterPreset.BALANCED,
    val samplingPeriod: SensorSamplingPeriod = SensorSamplingPeriod.GAME
)

enum class OrientationFilterPreset {
    RESPONSIVE,
    BALANCED,
    STABLE
}

enum class SensorSamplingPeriod {
    FASTEST,
    GAME,
    UI,
    NORMAL
}
