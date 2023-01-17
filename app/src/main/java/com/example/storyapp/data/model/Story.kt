package com.example.storyapp.data.model

import com.google.gson.annotations.SerializedName

data class Story(
    @field:SerializedName("id")
    val id: String = "",
    @field:SerializedName("name")
    val name: String = "",
    @field:SerializedName("description")
    val description: String = "",
    @field:SerializedName("photoUrl")
    val photoUrl: String = "",
    @field:SerializedName("createdAt")
    val createdAt: String = "",
    @field:SerializedName("lat")
    val lat: Double = 0.0,
    @field:SerializedName("lon")
    val lon: Double = 0.0,
)
