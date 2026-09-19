package com.felipeg.inclinometer4x4.car

import android.content.Intent
import androidx.car.app.Screen
import androidx.car.app.Session
import com.felipeg.common.domain.repository.OrientationRepository
import dagger.hilt.android.EntryPointAccessors
import javax.inject.Inject

class InclinometerSession : Session() {

    @Inject
    lateinit var orientationRepository: OrientationRepository

    override fun onCreateScreen(intent: Intent): Screen {
        val hiltEntryPoint = EntryPointAccessors.fromApplication(
            carContext.applicationContext,
            InclinometerCarEntryPoint::class.java
        )
        orientationRepository = hiltEntryPoint.orientationRepository()
        return MainAutoScreen(
            carContext = carContext,
            repository = orientationRepository,
            observeOrientation = hiltEntryPoint.observeOrientationUseCase()
        )
    }
}
