package com.felipeg.common.platform.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.felipeg.common.domain.model.OrientationFilterPreset
import com.felipeg.common.domain.model.SensorSamplingPeriod
import com.felipeg.common.domain.model.SensorSettings
import com.felipeg.common.domain.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

@Singleton
class DataStoreSettingsRepository @Inject constructor(
    @ApplicationContext context: Context
) : SettingsRepository {
    private val dataStore = context.inclinometerDataStore

    override val settings: Flow<SensorSettings> = dataStore.data
        .catch { error ->
            if (error is IOException) emit(androidx.datastore.preferences.core.emptyPreferences())
            else throw error
        }
        .map { preferences ->
            SensorSettings(
                orientationFilter = preferences[FILTER_KEY]
                    ?.let { value -> enumValues<OrientationFilterPreset>().firstOrNull { it.name == value } }
                    ?: OrientationFilterPreset.BALANCED,
                samplingPeriod = preferences[SAMPLING_PERIOD_KEY]
                    ?.let { value -> enumValues<SensorSamplingPeriod>().firstOrNull { it.name == value } }
                    ?: SensorSamplingPeriod.GAME
            )
        }

    override suspend fun setOrientationFilter(filter: OrientationFilterPreset) {
        dataStore.edit { it[FILTER_KEY] = filter.name }
    }

    override suspend fun setSamplingPeriod(period: SensorSamplingPeriod) {
        dataStore.edit { it[SAMPLING_PERIOD_KEY] = period.name }
    }

    private companion object {
        val FILTER_KEY = stringPreferencesKey("orientation_filter")
        val SAMPLING_PERIOD_KEY = stringPreferencesKey("sensor_sampling_period")
    }
}
