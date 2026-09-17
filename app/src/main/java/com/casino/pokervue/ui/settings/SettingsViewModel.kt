package com.casino.pokervue.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.casino.pokervue.data.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SettingsRepository(application.applicationContext)

    val darkMode: StateFlow<Boolean> = repository.darkMode
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val defaultOpponents: StateFlow<Int> = repository.defaultOpponents
        .stateIn(viewModelScope, SharingStarted.Eagerly, 1)

    val hapticFeedback: StateFlow<Boolean> = repository.hapticFeedback
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    fun setDarkMode(value: Boolean) = viewModelScope.launch { repository.setDarkMode(value) }
    fun setDefaultOpponents(value: Int) = viewModelScope.launch { repository.setDefaultOpponents(value) }
    fun setHapticFeedback(value: Boolean) = viewModelScope.launch { repository.setHapticFeedback(value) }
    fun resetAll() = viewModelScope.launch { repository.resetAll() }
}