package com.felipeg.inclinometer4x4.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felipeg.common.Angle
import com.felipeg.inclinometer4x4.domain.usecase.GetAngleStreamUseCase
import com.felipeg.inclinometer4x4.domain.usecase.CalibrateZeroUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AngleViewModel @Inject constructor(
    private val getAngleStream: GetAngleStreamUseCase,
    private val calibrateZero: CalibrateZeroUseCase
) : ViewModel() {

    private val _angleState = MutableStateFlow(Angle(0f, 0f, 0f))
    val angleState: StateFlow<Angle> = _angleState.asStateFlow()

    init {
        viewModelScope.launch {
            getAngleStream().collect { _angleState.value = it }
        }
    }

    fun onCalibrate() {
        calibrateZero.execute(_angleState.value)
    }
}
