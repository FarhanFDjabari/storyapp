package com.example.storyapp.data

import androidx.paging.PagingData
import com.example.storyapp.data.model.Story

object DataDummy {
    fun generateDummyStories(): List<Story> {
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
        return stories
    }

    fun generateDummyPagingStories(): PagingData<Story> = PagingData.from(generateDummyStories())
}