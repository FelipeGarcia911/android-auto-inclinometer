package com.felipeg.car

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Pane
import androidx.car.app.model.PaneTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import androidx.lifecycle.coroutineScope
import com.felipeg.common.SensorRepository
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class InclinometerScreen(carContext: CarContext) : Screen(carContext) {

    private val sensorRepository: SensorRepository

    init {
        val entryPoint = EntryPointAccessors.fromApplication(
            carContext,
            SensorRepositoryEntryPoint::class.java
        )
        sensorRepository = entryPoint.sensorRepository()
    }

    private var roll: Float = 0f
    private var pitch: Float = 0f

    override fun onGetTemplate(): Template {
        val row = Row.Builder()
            .setTitle("Inclinómetro")
            .addText("Roll: ${roll.toInt()}°, Pitch: ${pitch.toInt()}°")
            .build()

        val pane = Pane.Builder()
            .addRow(row)
            .build()

        return PaneTemplate.Builder(pane)
            .setHeaderAction(androidx.car.app.model.Action.APP_ICON)
            .setTitle("Inclinómetro 4x4")
            .build()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        lifecycle.coroutineScope.launch {
            sensorRepository.angleFlow.collect { angle ->
                roll = angle.roll
                pitch = angle.pitch
                invalidate()
            }
        }
    }
}
