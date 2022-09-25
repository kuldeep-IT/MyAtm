package com.kuldeep.atm1.utils.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map


class DataStoreManager(val context: Context) {

    private val Context.dataStore: DataStore<Preferences>  by preferencesDataStore(name = "USER_DATA_STORE")

    suspend fun saveDataString(key: String, value: Int) {
            val dataStoreKey = stringPreferencesKey(key)
        context.dataStore.edit {
            it[dataStoreKey] = value.toString()
        }
    }

    suspend fun getDataStore(key: String): String {
        val dataStoreKey = stringPreferencesKey(key)
        val getData = context.dataStore.data.first()
        return getData[dataStoreKey].toString()

    }

}