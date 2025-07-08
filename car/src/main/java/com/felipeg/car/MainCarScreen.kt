package com.felipeg.car

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Pane
import androidx.car.app.model.PaneTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.coroutineScope
import com.felipeg.common.SensorRepository
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.launch

class MainCarScreen(carContext: CarContext) : Screen(carContext) {

    private val sensorRepository: SensorRepository

    init {
        val entryPoint = EntryPointAccessors.fromApplication(
            carContext,
            SensorRepositoryEntryPoint::class.java
        )
        sensorRepository = entryPoint.sensorRepository()
        lifecycle.addObserver(MainCarScreenLifecycleObserver())
    }

    private var roll: Float = 0f
    private var pitch: Float = 0f
    private var gForceX: Float = 0f
    private var gForceY: Float = 0f

    override fun onGetTemplate(): Template {
        val inclinometerRow = Row.Builder()
            .setTitle("Inclinómetro")
            .addText("Roll: ${roll.toInt()}°, Pitch: ${pitch.toInt()}°")
            .build()

        val gForceRow = Row.Builder()
            .setTitle("Fuerza G")
            .addText("X: %.2f, Y: %.2f".format(gForceX, gForceY))
            .build()

        val pane = Pane.Builder()
            .addRow(inclinometerRow)
            .addRow(gForceRow)
            .build()

        return PaneTemplate.Builder(pane)
            .setHeaderAction(androidx.car.app.model.Action.APP_ICON)
            .setTitle("Inclinómetro 4x4")
            .build()
    }

    inner class MainCarScreenLifecycleObserver : DefaultLifecycleObserver {
        override fun onStart(owner: LifecycleOwner) {
            super.onStart(owner)
            owner.lifecycle.coroutineScope.launch {
                sensorRepository.angleFlow.collect { angle ->
                    roll = angle.roll
                    pitch = angle.pitch
                    invalidate()
                }
            }
            owner.lifecycle.coroutineScope.launch {
                sensorRepository.gForceFlow.collect { gForce ->
                    gForceX = gForce.x
                    gForceY = gForce.y
                    invalidate()
                }
            }
        }
    }
}
