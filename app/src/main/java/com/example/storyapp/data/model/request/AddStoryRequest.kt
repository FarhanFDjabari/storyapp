package com.example.storyapp.data.model.request

import com.google.gson.annotations.SerializedName

data class AddStoryRequest(
    @SerializedName("description")
    val description: String,
    @SerializedName("lat")
    val lat: Float?,
    @SerializedName("lon")
    val lon: Float?,
)
