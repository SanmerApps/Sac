package dev.sanmer.sac.repository

import dev.sanmer.sac.datastore.DarkMode
import dev.sanmer.sac.datastore.UserPreferencesDataSource
import dev.sanmer.sac.di.ApplicationScope
import dev.sanmer.sac.io.Endian
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepository @Inject constructor(
    private val userPreferencesDataSource: UserPreferencesDataSource,
    @ApplicationScope private val applicationScope: CoroutineScope
) {
    val data get() = userPreferencesDataSource.data

    fun setDarkTheme(value: DarkMode) = applicationScope.launch {
        userPreferencesDataSource.setDarkTheme(value)
    }

    fun setThemeColor(value: Int) = applicationScope.launch {
        userPreferencesDataSource.setThemeColor(value)
    }

    fun setEndian(value: Endian) = applicationScope.launch {
        userPreferencesDataSource.setEndian(value.name)
    }
}