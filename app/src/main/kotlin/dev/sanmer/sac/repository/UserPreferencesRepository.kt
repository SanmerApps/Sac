package dev.sanmer.sac.repository

import dev.sanmer.sac.datastore.DarkMode
import dev.sanmer.sac.datastore.UserPreferencesDataSource
import dev.sanmer.sac.io.Endian
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepository @Inject constructor(
    private val userPreferencesDataSource: UserPreferencesDataSource
) {
    val data get() = userPreferencesDataSource.data

    suspend fun setDarkTheme(value: DarkMode) = userPreferencesDataSource.setDarkTheme(value)

    suspend fun setThemeColor(value: Int) = userPreferencesDataSource.setThemeColor(value)

    suspend fun setEndian(value: Endian) = userPreferencesDataSource.setEndian(value.name)
}