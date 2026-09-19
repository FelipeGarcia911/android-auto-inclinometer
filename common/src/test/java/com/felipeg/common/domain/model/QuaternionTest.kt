package com.felipeg.common.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class QuaternionTest {
    @Test
    fun `identity leaves a quaternion unchanged`() {
        val rotation = Quaternion.fromEulerDegrees(pitch = 12f, roll = -7f, azimuth = 30f)

        assertQuaternionEquals(rotation, Quaternion.Identity * rotation)
        assertQuaternionEquals(rotation, rotation * Quaternion.Identity)
    }

    @Test
    fun `inverse multiplied by quaternion returns identity`() {
        val rotation = Quaternion.fromEulerDegrees(pitch = 20f, roll = 35f, azimuth = -15f)

        assertQuaternionEquals(Quaternion.Identity, rotation.inverse() * rotation)
    }

    @Test
    fun `multiplication composes rotations`() {
        val quarterTurn = Quaternion.fromEulerDegrees(pitch = 0f, roll = 90f)

        val halfTurn = quarterTurn * quarterTurn

        assertEquals(1f, halfTurn.x, 0.0001f)
        assertEquals(0f, halfTurn.y, 0.0001f)
        assertEquals(0f, halfTurn.z, 0.0001f)
        assertEquals(0f, halfTurn.w, 0.0001f)
    }

    @Test
    fun `normalization produces a unit quaternion`() {
        val normalized = Quaternion(1f, 2f, 3f, 4f).normalized()

        assertEquals(1f, normalized.magnitude, 0.0001f)
    }

    private fun assertQuaternionEquals(expected: Quaternion, actual: Quaternion) {
        assertEquals(expected.x, actual.x, 0.0001f)
        assertEquals(expected.y, actual.y, 0.0001f)
        assertEquals(expected.z, actual.z, 0.0001f)
        assertEquals(expected.w, actual.w, 0.0001f)
    }
}
