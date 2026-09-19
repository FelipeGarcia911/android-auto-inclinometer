package com.felipeg.common.data.source

import com.felipeg.common.data.model.RawRotation
import kotlinx.coroutines.flow.Flow

interface OrientationSensorDataSource {
    val rotations: Flow<RawRotation>

    fun start()
    fun stop()
}
