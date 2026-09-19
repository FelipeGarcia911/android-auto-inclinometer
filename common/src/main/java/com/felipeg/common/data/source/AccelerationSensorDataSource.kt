package com.felipeg.common.data.source

import com.felipeg.common.data.model.RawAcceleration
import kotlinx.coroutines.flow.Flow

interface AccelerationSensorDataSource {
    val accelerations: Flow<RawAcceleration>
}
