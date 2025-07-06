package com.felipeg.inclinometer4x4.presentation.viewmodel

import android.content.pm.ActivityInfo
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felipeg.common.Angle
import com.felipeg.inclinometer4x4.domain.usecase.GetAngleStreamUseCase
import com.felipeg.inclinometer4x4.domain.usecase.CalibrateZeroUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AngleViewModel @Inject constructor(
    private val getAngleStream: GetAngleStreamUseCase,
    private val calibrateZero: CalibrateZeroUseCase
) : ViewModel() {

    private val _angleState = MutableStateFlow(Angle(0f, 0f, 0f))
    val angleState: StateFlow<Angle> = _angleState.asStateFlow()

    private val _orientationState = MutableStateFlow(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
    val orientationState: StateFlow<Int> = _orientationState.asStateFlow()

    init {
        viewModelScope.launch {
            getAngleStream().collect { _angleState.value = it }
        }
    }

    fun onCalibrate() {
        calibrateZero.execute(_angleState.value)
    }

    fun toggleOrientation() {
        _orientationState.update { currentOrientation ->
            if (currentOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            } else {
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        }
        // Recalibrate automatically after orientation change
        onCalibrate()
    }
}
