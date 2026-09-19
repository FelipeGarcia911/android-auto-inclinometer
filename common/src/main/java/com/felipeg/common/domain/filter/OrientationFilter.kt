package com.felipeg.common.domain.filter

import com.felipeg.common.domain.model.Orientation

interface OrientationFilter {
    fun filter(orientation: Orientation): Orientation
    fun reset()
}
