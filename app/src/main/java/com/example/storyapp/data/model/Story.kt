package com.example.storyapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "story")
data class Story(

    @field:SerializedName("id")
    @PrimaryKey val id: String = "",
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
