package com.felipeg.inclinometer4x4.car

import android.content.Intent
import androidx.car.app.Screen
import androidx.car.app.Session
import com.felipeg.common.repository.FSensorRepository
import dagger.hilt.android.EntryPointAccessors
import javax.inject.Inject

class InclinometerSession : Session() {

    @Inject
    lateinit var sensorRepository: FSensorRepository

    override fun onCreateScreen(intent: Intent): Screen {
        val hiltEntryPoint = EntryPointAccessors.fromApplication(
            carContext.applicationContext,
            InclinometerCarEntryPoint::class.java
        )
        sensorRepository = hiltEntryPoint.sensorRepository()
        return MainAutoScreen(carContext, sensorRepository)
    }
}
