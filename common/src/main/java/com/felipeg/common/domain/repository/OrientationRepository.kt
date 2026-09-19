package com.felipeg.common.domain.repository

import com.felipeg.common.domain.model.DeviceRotation
import com.felipeg.common.domain.model.Orientation
import kotlinx.coroutines.flow.Flow

interface OrientationRepository {
    val orientations: Flow<Orientation>

    fun start()
    fun stop()
    fun updateDeviceRotation(rotation: DeviceRotation)
}
