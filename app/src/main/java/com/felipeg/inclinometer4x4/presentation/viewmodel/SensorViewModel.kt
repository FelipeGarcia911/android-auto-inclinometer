package com.felipeg.inclinometer4x4.presentation.viewmodel

import android.content.pm.ActivityInfo
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felipeg.common.Angle
import com.felipeg.inclinometer4x4.domain.usecase.GetAngleStreamUseCase
import com.felipeg.inclinometer4x4.domain.usecase.CalibrateZeroUseCase
import com.felipeg.common.GForce
import com.felipeg.inclinometer4x4.domain.usecase.CalibrateResetUseCase
import com.felipeg.inclinometer4x4.domain.usecase.GetGForceStreamUseCase
import com.felipeg.inclinometer4x4.domain.usecase.SetDeviceRotationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.sqrt

@HiltViewModel
class SensorViewModel @Inject constructor(
    private val getAngleStream: GetAngleStreamUseCase,
    private val getGForceStream: GetGForceStreamUseCase,
    private val calibrateZero: CalibrateZeroUseCase,
    private val calibrateReset: CalibrateResetUseCase,
    private val setDeviceRotation: SetDeviceRotationUseCase

) : ViewModel() {

    private val _angleState = MutableStateFlow(Angle(0f, 0f, 0f))
    val angleState: StateFlow<Angle> = _angleState.asStateFlow()

    private val _gForceState = MutableStateFlow(GForce(0f, 0f))
    val gForceState: StateFlow<GForce> = _gForceState.asStateFlow()

    private val _maxGForceState = MutableStateFlow(0f)
    val maxGForceState: StateFlow<Float> = _maxGForceState.asStateFlow()

    private val _orientationState = MutableStateFlow(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
    val orientationState: StateFlow<Int> = _orientationState.asStateFlow()

    init {
        viewModelScope.launch {
            getAngleStream().collect { _angleState.value = it }
        }
        viewModelScope.launch {
            getGForceStream().collect { gForce ->
                _gForceState.value = gForce
                val currentGForce = sqrt(gForce.x * gForce.x + gForce.y * gForce.y)
                if (currentGForce > _maxGForceState.value) {
                    _maxGForceState.value = currentGForce
                }
            }
        }
    }

    fun onCalibrate() {
        calibrateZero.execute()
        _maxGForceState.value = 0f
    }

    fun onReset() {
        calibrateReset.execute()
        _maxGForceState.value = 0f
    }

    /**
     * Updates the device's rotation in the repository.
     * This should be called from the UI whenever the rotation changes.
     * @param rotation The current display rotation value.
     */
    fun onRotationChanged(rotation: Int) {
        setDeviceRotation.execute(rotation)
    }

    fun toggleOrientation() {
        _orientationState.update { currentOrientation ->
            if (currentOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            } else {
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        }
    }
}
