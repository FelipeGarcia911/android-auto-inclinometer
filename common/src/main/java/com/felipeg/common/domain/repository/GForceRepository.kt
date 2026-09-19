package com.felipeg.common.domain.repository

import com.felipeg.common.domain.model.GForceReading
import kotlinx.coroutines.flow.Flow

interface GForceRepository {
    val readings: Flow<GForceReading>

    fun resetPeak()
}
