package com.felipeg.inclinometer4x4.presentation.viewmodel

import android.content.pm.ActivityInfo
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felipeg.common.model.Angle
import com.felipeg.common.model.GForce
import com.felipeg.inclinometer4x4.domain.repository.FSensorRepository
import com.felipeg.inclinometer4x4.domain.usecase.CalibrateResetUseCase
import com.felipeg.inclinometer4x4.domain.usecase.CalibrateZeroUseCase
import com.felipeg.inclinometer4x4.domain.usecase.GetGForceStreamUseCase
import com.felipeg.inclinometer4x4.domain.usecase.SetDeviceRotationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.sqrt

@HiltViewModel
class SensorViewModel @Inject constructor(
    private val fSensorRepository: FSensorRepository,
    private val getGForceStream: GetGForceStreamUseCase,
    private val calibrateZero: CalibrateZeroUseCase,
    private val calibrateReset: CalibrateResetUseCase,
    private val setDeviceRotation: SetDeviceRotationUseCase
) : ViewModel() {

    private val _offsetAngleState = MutableStateFlow(Angle(0f, 0f, 0f))

    private val _angleState = MutableStateFlow(Angle(0f, 0f, 0f))
    val angleState: StateFlow<Angle> = _angleState.asStateFlow()

    private val _gForceState = MutableStateFlow(GForce(0f, 0f))
    val gForceState: StateFlow<GForce> = _gForceState.asStateFlow()

    private val _maxGForceState = MutableStateFlow(0f)
    val maxGForceState: StateFlow<Float> = _maxGForceState.asStateFlow()

    private val _orientationState = MutableStateFlow(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
    val orientationState: StateFlow<Int> = _orientationState.asStateFlow()

    init {
        // Collect the flow from the new repository
        fSensorRepository.orientationFlow
            .onEach { angle -> _angleState.value =
            Angle(
                    azimuth = angle.azimuth + _offsetAngleState.value.azimuth,
                    pitch = angle.pitch + _offsetAngleState.value.pitch,
                    roll = angle.roll + _offsetAngleState.value.roll
                )
            }
            .launchIn(viewModelScope)

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

    fun startSensor() {
        fSensorRepository.start()
    }

    fun stopSensor() {
        fSensorRepository.stop()
    }

    fun calibrateZero() {
        _offsetAngleState.update {
            it.copy(
                pitch = it.pitch - _angleState.value.pitch,
                roll = it.roll - _angleState.value.roll
            )
        }
    }

    fun resetCalibration() {
        _offsetAngleState.value = Angle(0f, 0f, 0f)
        _maxGForceState.value = 0f
    }

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
