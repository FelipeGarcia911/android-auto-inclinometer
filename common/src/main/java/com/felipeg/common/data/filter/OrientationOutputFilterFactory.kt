package com.felipeg.common.data.filter

import com.felipeg.common.domain.filter.OrientationFilter
import com.felipeg.common.domain.model.OrientationFilterPreset

class OrientationOutputFilterFactory {
    fun create(preset: OrientationFilterPreset): OrientationFilter =
        ExponentialOrientationFilter(
            alpha = when (preset) {
                OrientationFilterPreset.RESPONSIVE -> 0.65f
                OrientationFilterPreset.BALANCED -> 0.35f
                OrientationFilterPreset.STABLE -> 0.15f
            }
        )
}
