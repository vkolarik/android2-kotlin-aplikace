package cz.mendelu.project.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first


private val Context.dataStore by preferencesDataStore(name = "settings")

class DataStoreRepositoryImpl(private val context: Context) : IDataStoreRepository {
    override suspend fun setFirstRun() {
        val preferencesKey = booleanPreferencesKey(DataStoreConstants.FIRST_RUN)
        context.dataStore.edit { preferences ->
            preferences[preferencesKey] = false
        }
    }

    override suspend fun getFirstRun(): Boolean {
        return try {

            val preferencesKey = booleanPreferencesKey(DataStoreConstants.FIRST_RUN)
            val preferences = context.dataStore.data.first()
            if (!preferences.contains(
                    preferencesKey
                )
            ) true else preferences[preferencesKey]!!
        } catch (e: Exception) {
            e.printStackTrace()
            true
        }
    }
}

