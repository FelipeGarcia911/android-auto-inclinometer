package com.felipeg.common.domain.repository

import com.felipeg.common.domain.model.OrientationFilterType
import com.felipeg.common.domain.model.SensorSamplingPeriod
import com.felipeg.common.domain.model.SensorSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val settings: Flow<SensorSettings>

    suspend fun setOrientationFilter(filter: OrientationFilterType)
    suspend fun setSamplingPeriod(period: SensorSamplingPeriod)
}
