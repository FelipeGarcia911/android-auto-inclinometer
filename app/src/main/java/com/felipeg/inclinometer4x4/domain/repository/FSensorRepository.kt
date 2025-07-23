package com.felipeg.inclinometer4x4.domain.repository

import com.felipeg.common.model.Angle
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

    /**
     * Sets the device's current rotation to adjust sensor readings accordingly.
     *
     * @param rotation The current rotation of the device (e.g., Surface.ROTATION_0).
     */
    fun setDeviceRotation(rotation: Int)
}
