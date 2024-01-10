package com.example.timer.feature_timer.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.timer.feature_timer.Timer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class TimerRepository private constructor (private val context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    val userPreferencesFlow: Flow<List<Timer>> = context.dataStore.data.map { timers ->
            mapTimer(timers)
        }

    suspend fun addTimer(name: String, duration: Long) {
        val timer = longPreferencesKey(name)

        context.dataStore.edit { preferences ->
            preferences[timer] = duration
        }
    }

    suspend fun removeTimer(name: String) {
        val timer = longPreferencesKey(name)

        context.dataStore.edit { preferences ->
            preferences.remove(timer)
        }
    }

    suspend fun removeAllTimers() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }


    private fun mapTimer(preferences: Preferences): List<Timer> {
        val timers = mutableListOf<Timer>()
        preferences.asMap().forEach { timer ->
            Log.d(" ", "mapTimer:  ${timer.key} ${timer.value}")
            val duration: Long = preferences[timer.key].toString().toLong()
//            val duration = preferences[timer.value] ?: 0
            timers.add(Timer(timer.key.toString(), duration))
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