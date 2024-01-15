package com.example.timer.feature_timer.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.timer.feature_timer.Timer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class TimerRepository private constructor (private val context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    private val timerCountKey = intPreferencesKey("timerCount")


    val userPreferencesFlow: Flow<List<Timer>> = context.dataStore.data.map { timers ->
            mapTimer(timers)
        }

    suspend fun addTimer() {
        val timerCount = context.dataStore.data.map { preferences ->
            preferences[timerCountKey] ?: 0
        }

        val timerName = stringPreferencesKey("timerName_$timerCount")
        val timerDuration = longPreferencesKey("timerDuration_$timerCount")

        context.dataStore.edit { preferences ->
            preferences[timerName] = ""
            preferences[timerDuration] = 0
            preferences[timerCountKey] = timerCount.toString().toInt() + 1
        }
    }

    suspend fun updateTimer(timerCount: Int, name: String, duration: Long) {
        val timerName = stringPreferencesKey("timerName_$timerCount")
        val timerDuration = longPreferencesKey("timerDuration_$timerCount")

        context.dataStore.edit { preferences ->
            preferences[timerName] = name
            preferences[timerDuration] = duration
        }
    }

    suspend fun removeTimer(timerCount: Int) {
        val timerName = stringPreferencesKey("timerName_$timerCount")
        val timerDuration = longPreferencesKey("timerDuration_$timerCount")

        context.dataStore.edit { preferences ->
            preferences.remove(timerName)
            preferences.remove(timerDuration)
            preferences[timerCountKey] = timerCount - 1
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
            if (timer.key.toString().contains("timerDuration") || timer.key.toString().contains("timerCount")) return@forEach
//            Log.d("TAG", "mapTimer: ${timer.key} ${timer.value}")
            val timerCount = timer.key.toString().replace("timerName_", "").toInt()

            val name: String = timer.value.toString()
            val duration: Long = preferences[longPreferencesKey("timerDuration_$timerCount")].toString().toLong()
//            val duration = preferences[timer.value] ?: 0
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