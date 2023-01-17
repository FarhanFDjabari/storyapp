package com.example.storyapp.data.services.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.storyapp.data.services.local.UserPreference.Companion.KEY_TOKEN
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class UserPreference private constructor(private val store: DataStore<Preferences>) {

    suspend fun saveToken(token: String?) {
        store.edit { preference ->
            token?.let { preference[KEY_TOKEN] = it }
        }
    }

    suspend fun getToken(): String? {
        val token = store.data.first()[KEY_TOKEN]
        if (token.isNullOrEmpty()) return null
        return token
    }

    suspend fun updateToken(token: String?) {
        store.edit { preference ->
            preference[KEY_TOKEN] = token?: ""
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        private val KEY_TOKEN = stringPreferencesKey("token")

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}