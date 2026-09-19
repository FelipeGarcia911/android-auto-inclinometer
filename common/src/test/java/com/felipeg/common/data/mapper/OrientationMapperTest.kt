package com.felipeg.common.data.mapper

import com.felipeg.common.data.model.RawOrientation
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.math.PI

class OrientationMapperTest {
    private val mapper = OrientationMapper()

    @Test
    fun `converts radians to degrees`() {
        assertEquals(180f, mapper.radiansToDegrees(PI.toFloat()), 0.001f)
        assertEquals(-90f, mapper.radiansToDegrees((-PI / 2).toFloat()), 0.001f)
    }

    @Test
    fun `maps invalid sensor values to zero`() {
        val orientation = mapper.map(
            RawOrientation(Float.NaN, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY)
        )

        assertEquals(0f, orientation.azimuth, 0f)
        assertEquals(0f, orientation.pitch, 0f)
        assertEquals(0f, orientation.roll, 0f)
    }
}
