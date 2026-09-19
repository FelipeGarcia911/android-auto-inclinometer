package com.felipeg.common.domain.calculator

import com.felipeg.common.domain.model.GForce
import org.junit.Assert.assertEquals
import org.junit.Test

class GForceCalculatorTest {
    private val calculator = GForceCalculator()

    @Test
    fun `converts acceleration to standard gravity`() {
        val force = calculator.fromAcceleration(9.80665f, -19.6133f)

        assertEquals(1f, force.x, 0.0001f)
        assertEquals(-2f, force.y, 0.0001f)
    }

    @Test
    fun `calculates vector magnitude`() {
        assertEquals(5f, calculator.magnitude(GForce(3f, 4f)), 0.0001f)
    }

    @Test
    fun `peak never decreases`() {
        assertEquals(6f, calculator.nextPeak(GForce(3f, 4f), 6f), 0.0001f)
        assertEquals(5f, calculator.nextPeak(GForce(3f, 4f), 2f), 0.0001f)
    }
}
