package com.felipeg.common.domain.repository

import com.felipeg.common.domain.model.Quaternion
import kotlinx.coroutines.flow.Flow

interface CalibrationRepository {
    val referenceRotation: Flow<Quaternion?>

    suspend fun saveReference(rotation: Quaternion)
    suspend fun reset()
}
