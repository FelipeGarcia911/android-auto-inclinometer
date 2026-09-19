package com.felipeg.common.domain.calculator

import com.felipeg.common.domain.model.DeviceRotation
import com.felipeg.common.domain.model.Orientation

class RotationTransformer {
    fun transform(orientation: Orientation, rotation: DeviceRotation): Orientation = when (rotation) {
        DeviceRotation.ROTATION_0 -> orientation
        DeviceRotation.ROTATION_90 -> orientation.copy(
            pitch = -orientation.roll,
            roll = orientation.pitch
        )
        DeviceRotation.ROTATION_180 -> orientation.copy(
            pitch = -orientation.pitch,
            roll = -orientation.roll
        )
        DeviceRotation.ROTATION_270 -> orientation.copy(
            pitch = orientation.roll,
            roll = -orientation.pitch
        )
    }
}
