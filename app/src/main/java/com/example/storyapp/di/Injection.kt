package com.example.storyapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.storyapp.data.services.local.StoryDatabase
import com.example.storyapp.data.services.local.UserPreference
import com.example.storyapp.data.services.remote.ApiConfig
import com.example.storyapp.repository.StoryRepository
import com.example.storyapp.repository.UserRepository

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

object Injection {
    fun provideUserRepository(context: Context): UserRepository {
        val apiServices = ApiConfig.getApiService()
        return UserRepository.getInstance(apiServices, UserPreference.getInstance(context.dataStore))
    }

    fun provideStoryRepository(context: Context): StoryRepository {
        val apiServices = ApiConfig.getApiService()
        val database = StoryDatabase.getDatabase(context)
        return StoryRepository.getInstance(apiServices, UserPreference.getInstance(context.dataStore), database)
    }

}