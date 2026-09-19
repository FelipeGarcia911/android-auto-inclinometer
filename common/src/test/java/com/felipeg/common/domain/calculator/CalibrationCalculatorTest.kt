package com.felipeg.common.domain.calculator

import com.felipeg.common.domain.model.Orientation
import org.junit.Assert.assertEquals
import org.junit.Test

class CalibrationCalculatorTest {
    private val calculator = CalibrationCalculator()

    @Test
    fun `calibration offset makes current orientation zero`() {
        val current = Orientation(12f, -7f, 3f)
        val offset = calculator.recalibrate(current, Orientation.Zero)

        assertEquals(Orientation.Zero, calculator.apply(current, offset))
    }

    @Test
    fun `recalibration accounts for the previous offset`() {
        val previousOffset = Orientation(2f, 3f, 0f)
        val displayedOrientation = Orientation(5f, -4f, 0f)

        assertEquals(
            Orientation(-3f, 7f, 0f),
            calculator.recalibrate(displayedOrientation, previousOffset)
        )
    }
}
