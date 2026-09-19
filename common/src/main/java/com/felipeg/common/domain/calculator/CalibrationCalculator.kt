package com.felipeg.common.domain.calculator

import com.felipeg.common.domain.model.Orientation

class CalibrationCalculator {
    fun apply(orientation: Orientation, offset: Orientation): Orientation = Orientation(
        pitch = orientation.pitch + offset.pitch,
        roll = orientation.roll + offset.roll,
        azimuth = orientation.azimuth + offset.azimuth
    )

    fun recalibrate(current: Orientation, previousOffset: Orientation): Orientation = Orientation(
        pitch = previousOffset.pitch - current.pitch,
        roll = previousOffset.roll - current.roll,
        azimuth = previousOffset.azimuth - current.azimuth
    )
}
