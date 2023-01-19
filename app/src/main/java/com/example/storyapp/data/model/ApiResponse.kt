package com.example.storyapp.data.model

import com.google.gson.annotations.SerializedName

data class BaseResponse (
    @field:SerializedName("error")
    val error: Boolean? = null,
    @field:SerializedName("message")
    val message: String? = null,
)

data class LoginResponse(
    @field:SerializedName("error")
    val error: Boolean = false,
    @field:SerializedName("message")
    val message: String = "",
    @field:SerializedName("loginResult")
    val loginResult: LoginResult? = null,
)

data class StoryResponses(
    @field:SerializedName("error")
    val error: Boolean = false,
    @field:SerializedName("message")
    val message: String? = null,
    @field:SerializedName("listStory")
    val listStory: List<Story>? = null,
)

data class StoryResponse(
    @field:SerializedName("error")
    val error: Boolean = false,
    @field:SerializedName("message")
    val message: String = "",
    @field:SerializedName("story")
    val story: Story? = null,
)