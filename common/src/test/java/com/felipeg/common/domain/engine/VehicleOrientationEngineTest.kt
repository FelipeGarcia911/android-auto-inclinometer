package com.felipeg.common.domain.engine

import com.felipeg.common.domain.model.DeviceRotation
import com.felipeg.common.domain.model.Quaternion
import org.junit.Assert.assertEquals
import org.junit.Test

class VehicleOrientationEngineTest {
    private val engine = VehicleOrientationEngine()

    @Test
    fun `same reference and current rotation produce zero tilt`() {
        val rotation = Quaternion.fromEulerDegrees(23f, -17f, 41f)

        val result = engine.calculate(rotation, rotation, DeviceRotation.ROTATION_0)

        assertTilt(result.calibratedOrientation.pitch, result.calibratedOrientation.roll, 0f, 0f)
    }

    @Test
    fun `positive and negative roll preserve their sign`() {
        assertRelativeTilt(pitch = 0f, roll = 10f)
        assertRelativeTilt(pitch = 0f, roll = -10f)
    }

    @Test
    fun `positive and negative pitch preserve their sign`() {
        assertRelativeTilt(pitch = 10f, roll = 0f)
        assertRelativeTilt(pitch = -10f, roll = 0f)
    }

    @Test
    fun `arbitrary mount calibrates to zero and preserves relative movement`() {
        val reference = Quaternion.fromEulerDegrees(pitch = 28f, roll = -16f, azimuth = 37f)
        val movement = Quaternion.fromEulerDegrees(pitch = 8f, roll = -6f)
        val current = reference * movement

        val atReference = engine.calculate(reference, reference, DeviceRotation.ROTATION_0)
        val afterMovement = engine.calculate(current, reference, DeviceRotation.ROTATION_0)

        assertTilt(atReference.calibratedOrientation.pitch, atReference.calibratedOrientation.roll, 0f, 0f)
        assertTilt(afterMovement.calibratedOrientation.pitch, afterMovement.calibratedOrientation.roll, 8f, -6f)
    }

    @Test
    fun `display rotation remaps axes before extracting euler angles`() {
        val current = Quaternion.fromEulerDegrees(pitch = 0f, roll = 10f)

        val result = engine.calculate(current, Quaternion.Identity, DeviceRotation.ROTATION_90)

        assertTilt(result.calibratedOrientation.pitch, result.calibratedOrientation.roll, -10f, 0f)
    }

    private fun assertRelativeTilt(pitch: Float, roll: Float) {
        val current = Quaternion.fromEulerDegrees(pitch = pitch, roll = roll)
        val result = engine.calculate(current, Quaternion.Identity, DeviceRotation.ROTATION_0)
        assertTilt(result.calibratedOrientation.pitch, result.calibratedOrientation.roll, pitch, roll)
    }

    private fun assertTilt(
        actualPitch: Float,
        actualRoll: Float,
        expectedPitch: Float,
        expectedRoll: Float
    ) {
        assertEquals(expectedPitch, actualPitch, 0.01f)
        assertEquals(expectedRoll, actualRoll, 0.01f)
    }
}
