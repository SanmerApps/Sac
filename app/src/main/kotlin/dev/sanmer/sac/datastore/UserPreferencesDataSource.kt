package dev.sanmer.sac.datastore

import androidx.datastore.core.DataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserPreferencesDataSource @Inject constructor(
    private val userPreferences: DataStore<UserPreferences>
) {
    val data get() = userPreferences.data.map { it.toExt() }

    suspend fun setDarkTheme(value: DarkMode) = withContext(Dispatchers.IO) {
        userPreferences.updateData {
            it.new {
                darkMode = value
            }
        }
    }

    suspend fun setThemeColor(value: Int) = withContext(Dispatchers.IO) {
        userPreferences.updateData {
            it.new {
                themeColor = value
            }
        }
    }

    suspend fun setEndian(value: String) = withContext(Dispatchers.IO) {
        userPreferences.updateData {
            it.new {
                endian = value
            }
        }
    }
}