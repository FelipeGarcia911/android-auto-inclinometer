package com.felipeg.common.platform.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import com.felipeg.common.domain.model.Orientation
import com.felipeg.common.domain.repository.CalibrationRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

@Singleton
class DataStoreCalibrationRepository @Inject constructor(
    @ApplicationContext context: Context
) : CalibrationRepository {
    private val dataStore = context.inclinometerDataStore

    override val offset: Flow<Orientation> = dataStore.data
        .catch { error ->
            if (error is IOException) emit(androidx.datastore.preferences.core.emptyPreferences())
            else throw error
        }
        .map { preferences ->
            Orientation(
                pitch = preferences[PITCH_OFFSET_KEY] ?: 0f,
                roll = preferences[ROLL_OFFSET_KEY] ?: 0f,
                azimuth = preferences[AZIMUTH_OFFSET_KEY] ?: 0f
            )
        }

    override suspend fun saveOffset(offset: Orientation) {
        dataStore.edit { preferences ->
            preferences[PITCH_OFFSET_KEY] = offset.pitch
            preferences[ROLL_OFFSET_KEY] = offset.roll
            preferences[AZIMUTH_OFFSET_KEY] = offset.azimuth
        }
    }

    override suspend fun reset() {
        saveOffset(Orientation.Zero)
    }

    private companion object {
        val PITCH_OFFSET_KEY = floatPreferencesKey("calibration_pitch_offset")
        val ROLL_OFFSET_KEY = floatPreferencesKey("calibration_roll_offset")
        val AZIMUTH_OFFSET_KEY = floatPreferencesKey("calibration_azimuth_offset")
    }
}
