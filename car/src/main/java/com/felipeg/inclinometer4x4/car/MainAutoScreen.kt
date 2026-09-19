package com.felipeg.inclinometer4x4.car

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.Pane
import androidx.car.app.model.PaneTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.felipeg.common.domain.repository.OrientationRepository
import com.felipeg.common.domain.usecase.ObserveOrientationUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainAutoScreen(
    carContext: CarContext,
    private val repository: OrientationRepository,
    private val observeOrientation: ObserveOrientationUseCase
) : Screen(carContext) {

    private var pitch = 0f
    private var roll = 0f

    private val screenScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var collectionJob: Job? = null

    init {
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                repository.start()
                collectionJob = screenScope.launch {
                    observeOrientation().collectLatest { orientation ->
                        pitch = orientation.pitch
                        roll = orientation.roll
                        invalidate()
                    }
                }
            }

            override fun onStop(owner: LifecycleOwner) {
                repository.stop()
                collectionJob?.cancel()
            }

            override fun onDestroy(owner: LifecycleOwner) {
                screenScope.cancel()
            }
        })
    }

    override fun onGetTemplate(): Template {
        val pitchRow = Row.Builder()
            .setTitle("Pitch")
            .addText(String.format("%.1f°", pitch))
            .build()

        val rollRow = Row.Builder()
            .setTitle("Roll")
            .addText(String.format("%.1f°", roll))
            .build()

        val pane = Pane.Builder()
            .addRow(pitchRow)
            .addRow(rollRow)
            .build()

        return PaneTemplate.Builder(pane)
            .setTitle("Inclinometer")
            .setHeaderAction(Action.APP_ICON)
            .build()
    }
}
