package com.example.storyapp.data

import com.example.storyapp.data.model.*
import com.example.storyapp.data.model.request.LoginRequest
import com.example.storyapp.data.model.request.RegisterRequest
import com.example.storyapp.data.services.remote.ApiServices
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class FakeApiService : ApiServices {

    override suspend fun login(loginRequest: LoginRequest): Response<LoginResponse> {
        return Response.success(
            LoginResponse(
                error = false,
                message = "success",
                loginResult = LoginResult(
                    userId = "it",
                    name = "user",
                    token = "token"
                )
            )
        )
    }

    override suspend fun register(registerRequest: RegisterRequest): Response<BaseResponse> {
        return Response.success(
            BaseResponse(
                error = false,
                message = "success",
            )
        )
    }

    override suspend fun addStoryAsGuest(
        description: RequestBody,
        latitude: Float?,
        longitude: Float?,
        file: MultipartBody.Part
    ): Response<BaseResponse> {
        return Response.success(
            BaseResponse(
                error = false,
                message = "success",
            )
        )
    }

    override suspend fun addStory(
        authToken: String,
        description: RequestBody,
        latitude: Float?,
        longitude: Float?,
        file: MultipartBody.Part
    ): Response<BaseResponse> {
        return Response.success(
            BaseResponse(
                error = false,
                message = "success",
            )
        )
    }

    override suspend fun getAllStories(
        authToken: String,
        page: Int,
        size: Int,
        location: Int
    ): Response<StoryResponses> {
        val stories: MutableList<Story> = arrayListOf()
        repeat(10) {
            stories.add(
                Story(
                    photoUrl = "photo-$it",
                    createdAt = "createdAt-$it",
                    name = "name-$it",
                    description = "desc-$it",
                    lon = null,
                    id = it.toString(),
                    lat = null,
                )
            )
        }

        val response = StoryResponses(error = false, message = null, stories)

        return Response.success(response)
    }

    override suspend fun getStoryDetail(authToken: String, id: String): Response<StoryResponse> {
        return Response.success(
            StoryResponse(
                error = false,
                message = "",
                story = Story(
                    photoUrl = "photo",
                    createdAt = "createdAt",
                    name = "name",
                    description = "desc",
                    lon = null,
                    id = "it.toString()",
                    lat = null,
                )
            )
        )
    }
}