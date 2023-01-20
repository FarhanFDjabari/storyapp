package com.example.storyapp.ui.features.maps.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.model.Story
import com.example.storyapp.repository.StoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapsViewModel(private val storyRepository: StoryRepository): ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _stories = MutableLiveData<List<Story>>()
    val stories: LiveData<List<Story>> = _stories

    private val _snackbarText = MutableLiveData<String>()
    val snackbarText: LiveData<String> = _snackbarText

    companion object {
        private const val TAG = "MapsViewModel"
    }

    fun getAllStories() {
        _isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.Main) {
            try {
                val result = storyRepository.getAllStoriesMap()
                _stories.postValue(result)
            } catch (e: Throwable) {
                _snackbarText.postValue(e.message.toString())
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}