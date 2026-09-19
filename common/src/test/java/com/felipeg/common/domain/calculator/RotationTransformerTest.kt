package com.felipeg.common.domain.calculator

import com.felipeg.common.domain.model.DeviceRotation
import com.felipeg.common.domain.model.Orientation
import org.junit.Assert.assertEquals
import org.junit.Test

class RotationTransformerTest {
    private val transformer = RotationTransformer()
    private val orientation = Orientation(pitch = 10f, roll = 20f, azimuth = 30f)

    @Test
    fun `rotates pitch and roll for every display rotation`() {
        assertEquals(Orientation(10f, 20f, 30f), transformer.transform(orientation, DeviceRotation.ROTATION_0))
        assertEquals(Orientation(-20f, 10f, 30f), transformer.transform(orientation, DeviceRotation.ROTATION_90))
        assertEquals(Orientation(-10f, -20f, 30f), transformer.transform(orientation, DeviceRotation.ROTATION_180))
        assertEquals(Orientation(20f, -10f, 30f), transformer.transform(orientation, DeviceRotation.ROTATION_270))
    }
}
