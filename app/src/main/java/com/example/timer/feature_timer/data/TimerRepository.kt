package com.example.timer.feature_timer.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.timer.feature_timer.Timer
import com.example.timer.feature_timer.data.TimerRepository.PreferencesKeys.TIMER_NAME
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class TimerRepository (private val dataStore: DataStore<Preferences>) {

    /**
     * Get the user preferences flow.
     */
    val userPreferencesFlow: Flow<Timer> = dataStore.data
        .catch { exception ->
\            if (exception is IOException) {
                Log.e("TAG", "Error reading preferences.", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { timers ->
            mapUserPreferences(timers)
        }



    private fun mapUserPreferences(preferences: Preferences): Timer {
        // Get the sort order from preferences and convert it to a [SortOrder] object
        val name = preferences[TIMER_NAME] ?: ""

        // Get our show completed value, defaulting to false if not set:
        val duration = preferences[TIMER_DURATION] ?: 0
        return Timer(name, duration)
    }

    companion object PreferencesKeys {
        val TIMER_NAME = stringPreferencesKey("timer_name")
        val TIMER_DURATION = longPreferencesKey("timer_duration")
    }
}