package com.felipeg.common.domain.repository

import com.felipeg.common.domain.model.DeviceRotation
import com.felipeg.common.domain.model.Orientation
import com.felipeg.common.domain.model.OrientationReading
import kotlinx.coroutines.flow.Flow

interface OrientationRepository {
    val readings: Flow<OrientationReading>
    val orientations: Flow<Orientation>

    fun start()
    fun stop()
    fun updateDeviceRotation(rotation: DeviceRotation)
}
