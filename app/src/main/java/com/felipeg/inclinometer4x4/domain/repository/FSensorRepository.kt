package com.felipeg.inclinometer4x4.domain.repository

import com.felipeg.common.Angle
import kotlinx.coroutines.flow.Flow

/**
 * Interface for a repository that provides orientation data from the device's sensors.
 */
interface FSensorRepository {

    /**
     * A flow that emits [Angle] updates.
     */
    val orientationFlow: Flow<Angle>

    /**
     * Starts listening for sensor updates.
     */
    fun start()

    /**
     * Stops listening for sensor updates.
     */
    fun stop()
}
