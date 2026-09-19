package com.felipeg.common.data.source

import com.felipeg.common.data.model.RawOrientation
import kotlinx.coroutines.flow.Flow

interface OrientationSensorDataSource {
    val orientations: Flow<RawOrientation>

    fun start()
    fun stop()
}
