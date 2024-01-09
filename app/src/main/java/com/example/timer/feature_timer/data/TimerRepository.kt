package com.example.timer.feature_timer.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.timer.feature_timer.Timer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val TIMER_NAME = stringPreferencesKey("timer_name")
private val TIMER_DURATION = longPreferencesKey("timer_duration")

class TimerRepository private constructor (private val context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    val userPreferencesFlow: Flow<List<Timer>> = context.dataStore.data.map { timers ->
            mapTimer(timers)
        }

    suspend fun addTimer(name: String, duration: Long) {

        context.dataStore.edit { preferences ->
            preferences[stringPreferencesKey(name)] = name
            preferences[TIMER_DURATION] = duration
        }
    }


    private fun mapTimer(preferences: Preferences): List<Timer> {
        val timers = mutableListOf<Timer>()
        preferences.asMap().forEach { _ ->
            val name = preferences[TIMER_NAME] ?: ""
            val duration = preferences[TIMER_DURATION] ?: 0
            timers.add(Timer(name, duration))
        }
        return timers
    }

    companion object {
        @Volatile
        private var INSTANCE: TimerRepository? = null

        fun getInstance(context: Context): TimerRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE?.let {
                    return it
                }

                val instance = TimerRepository(context)
                INSTANCE = instance
                instance
            }
        }
    }
}