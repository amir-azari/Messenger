package azari.amirhossein.messenger.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class UserPreferencesRepository @Inject constructor(@ApplicationContext private val context: Context) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

    private companion object {
        val USERNAME_KEY = stringPreferencesKey("username")
    }

    // Flow to retrieve the saved username
    val username: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[USERNAME_KEY]
        }

    // Suspend function to save the username
    suspend fun saveUsername(name: String) {
        context.dataStore.edit { preferences ->
            preferences[USERNAME_KEY] = name
        }
    }
}
