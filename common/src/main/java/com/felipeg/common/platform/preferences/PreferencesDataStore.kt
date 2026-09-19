package com.felipeg.common.platform.preferences

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

internal val Context.inclinometerDataStore by preferencesDataStore(name = "inclinometer_settings")
