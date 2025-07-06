package com.felipeg.car

import android.content.Intent
import androidx.car.app.Screen
import androidx.car.app.Session

class InclinometerSession : Session() {
    override fun onCreateScreen(intent: Intent): Screen {
        return InclinometerScreen(carContext)
    }
}
