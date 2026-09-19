package com.felipeg.inclinometer4x4.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felipeg.common.domain.model.DeviceRotation
import com.felipeg.common.domain.repository.OrientationRepository
import com.felipeg.common.domain.usecase.CalibrateUseCase
import com.felipeg.common.domain.usecase.ObserveGForceUseCase
import com.felipeg.common.domain.usecase.ObserveOrientationUseCase
import com.felipeg.common.domain.usecase.ResetCalibrationUseCase
import com.felipeg.common.domain.usecase.UpdateDeviceRotationUseCase
import com.felipeg.inclinometer4x4.presentation.model.DashboardUiState
import com.felipeg.inclinometer4x4.presentation.model.SensorDiagnosticsUiState
import com.felipeg.inclinometer4x4.presentation.model.ScreenOrientation
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SensorViewModel @Inject constructor(
    private val orientationRepository: OrientationRepository,
    observeOrientation: ObserveOrientationUseCase,
    observeGForce: ObserveGForceUseCase,
    private val calibrate: CalibrateUseCase,
    private val resetCalibration: ResetCalibrationUseCase,
    private val updateDeviceRotation: UpdateDeviceRotationUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        observeOrientation.readings()
            .onEach { reading ->
                _uiState.update {
                    it.copy(
                        orientation = reading.filteredOrientation,
                        diagnostics = SensorDiagnosticsUiState(
                            sensorActive = true,
                            sensorType = reading.sensorType,
                            samplingRateHz = reading.samplingRateHz,
                            currentRotation = reading.currentRotation,
                            referenceRotation = reading.referenceRotation,
                            rawOrientation = reading.rawOrientation,
                            calibratedOrientation = reading.calibratedOrientation,
                            filteredOrientation = reading.filteredOrientation,
                            deviceRotation = reading.deviceRotation
                        )
                    )
                }
            }
            .launchIn(viewModelScope)

        observeGForce()
            .onEach { reading ->
                _uiState.update {
                    it.copy(
                        gForce = reading.current,
                        maxGForce = reading.peakMagnitude
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun startSensors() {
        orientationRepository.start()
    }

    fun stopSensors() {
        orientationRepository.stop()
        _uiState.update { state ->
            state.copy(diagnostics = state.diagnostics.copy(sensorActive = false))
        }
    }

    fun calibrateZero() {
        val currentRotation = _uiState.value.diagnostics.currentRotation ?: return
        viewModelScope.launch { calibrate(currentRotation) }
    }

    fun resetCalibration() {
        viewModelScope.launch { resetCalibration.invoke() }
    }

    fun onRotationChanged(rotation: DeviceRotation) {
        updateDeviceRotation(rotation)
    }

    fun toggleScreenOrientation() {
        _uiState.update { state ->
            state.copy(
                screenOrientation = when (state.screenOrientation) {
                    ScreenOrientation.PORTRAIT -> ScreenOrientation.LANDSCAPE
                    ScreenOrientation.LANDSCAPE -> ScreenOrientation.PORTRAIT
                }
            )
        }
    }
}
