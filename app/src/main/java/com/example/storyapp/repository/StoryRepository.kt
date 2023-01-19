package com.example.storyapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.*
import com.example.storyapp.data.model.Story
import com.example.storyapp.data.model.request.AddStoryRequest
import com.example.storyapp.data.services.StoryRemoteMediator
import com.example.storyapp.data.services.local.StoryDatabase
import com.example.storyapp.data.services.local.UserPreference
import com.example.storyapp.data.services.remote.ApiServices
import kotlinx.coroutines.Dispatchers
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class StoryRepository private constructor(
    private val apiServices: ApiServices,
    private val pref: UserPreference,
    private val storyDatabase: StoryDatabase
    ) {

    fun getAllStories(): LiveData<PagingData<Story>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(pageSize = 10),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiServices, pref),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
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

        fun getInstance(apiServices: ApiServices, pref: UserPreference, storyDatabase: StoryDatabase): StoryRepository {
            if (instance == null) {
                synchronized(this) {
                    instance = StoryRepository(apiServices, pref, storyDatabase)
                }
            }
            return instance as StoryRepository
        }
    }
}