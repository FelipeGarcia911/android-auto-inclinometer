package com.felipeg.common.platform.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import com.felipeg.common.domain.model.Quaternion
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

    override val referenceRotation: Flow<Quaternion?> = dataStore.data
        .catch { error ->
            if (error is IOException) emit(androidx.datastore.preferences.core.emptyPreferences())
            else throw error
        }
        .map { preferences ->
            val x = preferences[REFERENCE_X_KEY]
            val y = preferences[REFERENCE_Y_KEY]
            val z = preferences[REFERENCE_Z_KEY]
            val w = preferences[REFERENCE_W_KEY]
            if (x == null || y == null || z == null || w == null) {
                null
            } else {
                Quaternion(x = x, y = y, z = z, w = w).normalized()
            }
        }

    override suspend fun saveReference(rotation: Quaternion) {
        val normalized = rotation.normalized()
        dataStore.edit { preferences ->
            preferences[REFERENCE_X_KEY] = normalized.x
            preferences[REFERENCE_Y_KEY] = normalized.y
            preferences[REFERENCE_Z_KEY] = normalized.z
            preferences[REFERENCE_W_KEY] = normalized.w
        }
    }

    override suspend fun reset() {
        dataStore.edit { preferences ->
            preferences.remove(REFERENCE_X_KEY)
            preferences.remove(REFERENCE_Y_KEY)
            preferences.remove(REFERENCE_Z_KEY)
            preferences.remove(REFERENCE_W_KEY)
        }
    }

    private companion object {
        val REFERENCE_X_KEY = floatPreferencesKey("calibration_reference_x")
        val REFERENCE_Y_KEY = floatPreferencesKey("calibration_reference_y")
        val REFERENCE_Z_KEY = floatPreferencesKey("calibration_reference_z")
        val REFERENCE_W_KEY = floatPreferencesKey("calibration_reference_w")
    }
}
