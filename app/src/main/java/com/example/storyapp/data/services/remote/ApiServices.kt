package com.example.storyapp.data.services.remote

import com.example.storyapp.data.model.BaseResponse
import com.example.storyapp.data.model.LoginResponse
import com.example.storyapp.data.model.StoryResponse
import com.example.storyapp.data.model.StoryResponses
import com.example.storyapp.data.model.request.LoginRequest
import com.example.storyapp.data.model.request.RegisterRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiServices {
    @POST("login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ) : Response<LoginResponse>

    @POST("register")
    suspend fun register(
        @Body registerRequest: RegisterRequest
    ) : Response<BaseResponse>

    @Multipart
    @POST("stories/guest")
    suspend fun addStoryAsGuest(
        @Part("description") description: RequestBody,
        @Part("lat") latitude: Float? = null,
        @Part("lon") longitude: Float? = null,
        @Part file: MultipartBody.Part
    ) : Response<BaseResponse>

    @Multipart
    @POST("stories")
    suspend fun addStory(
        @Header("Authorization") authToken: String,
        @Part("description") description: RequestBody,
        @Part("lat") latitude: Float? = null,
        @Part("lon") longitude: Float? = null,
        @Part file: MultipartBody.Part
    ) : Response<BaseResponse>

    @GET("stories")
    suspend fun getAllStories(
        @Header("Authorization") authToken: String,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 10,
        @Query("location") location: Int = 0
    ) : Response<StoryResponses>

    @GET("stories/{id}")
    suspend fun getStoryDetail(
        @Header("Authorization") authToken: String,
        @Path("id") id: String,
    ) : Response<StoryResponse>

}