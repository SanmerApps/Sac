package dev.sanmer.sac.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanmer.sac.datastore.DarkMode
import dev.sanmer.sac.repository.UserPreferencesRepository
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {
    init {
        Timber.d("SettingsViewModel init")
    }

    fun setDarkTheme(value: DarkMode) =
        userPreferencesRepository.setDarkTheme(value)

    fun setThemeColor(value: Int) =
        userPreferencesRepository.setThemeColor(value)
}