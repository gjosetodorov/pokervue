package com.casino.pokervue.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

object SettingsKeys {
    val DARK_MODE = booleanPreferencesKey("dark_mode")
    val DEFAULT_OPPONENTS = intPreferencesKey("default_opponents")
    val HAPTIC_FEEDBACK = booleanPreferencesKey("haptic_feedback")
}

object SettingsDefaults {
    const val DARK_MODE = true
    const val DEFAULT_OPPONENTS = 1
    const val HAPTIC_FEEDBACK = true
}

class SettingsRepository(private val context: Context) {

    val darkMode: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[SettingsKeys.DARK_MODE] ?: SettingsDefaults.DARK_MODE
    }

    val defaultOpponents: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[SettingsKeys.DEFAULT_OPPONENTS] ?: SettingsDefaults.DEFAULT_OPPONENTS
    }

    val hapticFeedback: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[SettingsKeys.HAPTIC_FEEDBACK] ?: SettingsDefaults.HAPTIC_FEEDBACK
    }

    suspend fun setDarkMode(value: Boolean) {
        context.dataStore.edit { it[SettingsKeys.DARK_MODE] = value }
    }

    suspend fun setDefaultOpponents(value: Int) {
        context.dataStore.edit { it[SettingsKeys.DEFAULT_OPPONENTS] = value }
    }

    suspend fun setHapticFeedback(value: Boolean) {
        context.dataStore.edit { it[SettingsKeys.HAPTIC_FEEDBACK] = value }
    }

    suspend fun resetAll() {
        context.dataStore.edit { it.clear() }
    }
}