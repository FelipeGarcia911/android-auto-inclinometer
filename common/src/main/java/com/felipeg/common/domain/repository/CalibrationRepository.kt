package com.felipeg.common.domain.repository

import com.felipeg.common.domain.model.Orientation
import kotlinx.coroutines.flow.Flow

interface CalibrationRepository {
    val offset: Flow<Orientation>

    suspend fun saveOffset(offset: Orientation)
    suspend fun reset()
}
