package com.example.storyapp.repository

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.asLiveData
import com.example.storyapp.R
import com.example.storyapp.data.model.Story
import com.example.storyapp.data.model.request.AddStoryRequest
import com.example.storyapp.data.services.local.UserPreference
import com.example.storyapp.data.services.remote.ApiServices
import kotlinx.coroutines.Dispatchers
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class StoryRepository private constructor(
    private val context: Context,
    private val apiServices: ApiServices,
    private val pref: UserPreference
    ) {

    suspend fun getAllStories(page: Int?, size: Int?, location: Boolean?): List<Story> {
        with(Dispatchers.IO) {
            try {
                val userToken = pref.getToken()
                val bearerToken = "Bearer $userToken"
                val locationInt = location?.compareTo(true) ?: 0
                val response = apiServices.getAllStories(
                    authToken = bearerToken
                )

                if (!response.isSuccessful) {
                    throw Exception(response.body()?.message.toString())
                }

                return response.body()?.listStory ?: listOf()
            } catch (e: Exception) {
                Log.e(this.javaClass.simpleName, "getAllStories: ${e.message}")
                throw Exception(e.message.toString())
            }
        }
    }

    suspend fun getStoryDetail(storyId: String): Story? {
        try {
            val userToken = pref.getToken()
            val bearerToken = "Bearer $userToken"
            val response = apiServices.getStoryDetail(authToken = bearerToken, id = storyId)

            if (!response.isSuccessful) {
                throw Exception(response.body()?.message.toString())
            }

            return response.body()?.story
        } catch (e: Throwable) {
            Log.e(this@StoryRepository.javaClass.simpleName, "getAllStories: ${e.message}")
            throw Exception(e.message.toString())
        }
    }

    suspend fun addNewStory(storyData: AddStoryRequest, photoPart: MultipartBody.Part): Boolean {
        with(Dispatchers.IO) {
            try {
                val userToken = pref.getToken()
                val bearerToken = "Bearer $userToken"
                val descriptionRequest = storyData.description.toRequestBody("text/plain".toMediaType())
                val response = apiServices.addStory(
                    authToken = bearerToken,
                    description = descriptionRequest,
                    file = photoPart,
                )

                if (!response.isSuccessful) {
                    Log.e(this@StoryRepository.javaClass.simpleName, "addNewStory: code: ${response.code()} body: ${response.body()?.message}")
                    throw Exception(response.body()?.message.toString())
                }

                return response.body()?.error == false
            } catch (e: Throwable) {
                Log.e(this@StoryRepository.javaClass.simpleName, "addNewStory: ${e.message}")
                throw Exception(e.message.toString())
            }
        }
    }

    suspend fun addNewStoryAsGuest(storyData: AddStoryRequest, photoPart: MultipartBody.Part): Boolean {
        with(Dispatchers.IO) {
            try {
                val response = apiServices.addStoryAsGuest(
                    description = storyData.description.toRequestBody("text/plain".toMediaType()),
                    latitude = storyData.lat,
                    longitude = storyData.lon,
                    file = photoPart,
                )

                if (!response.isSuccessful) {
                    throw Exception(response.body()?.message.toString())
                }

                return response.body()?.error ?: false
            } catch (e: Throwable) {
                Log.e(this@StoryRepository.javaClass.simpleName, "getAllStories: ${e.message}")
                throw Exception(e.message.toString())
            }
        }
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(context: Context, apiServices: ApiServices, pref: UserPreference): StoryRepository {
            if (instance == null) {
                synchronized(this) {
                    instance = StoryRepository(context.applicationContext, apiServices, pref)
                }
            }
            return instance as StoryRepository
        }
    }
}