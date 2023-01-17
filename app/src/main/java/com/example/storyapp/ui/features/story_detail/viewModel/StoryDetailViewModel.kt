package com.example.storyapp.ui.features.story_detail.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.model.Story
import com.example.storyapp.data.model.request.AddStoryRequest
import com.example.storyapp.repository.StoryRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class StoryDetailViewModel(private val storyRepository: StoryRepository): ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _storyData = MutableLiveData<Story>()
    val storyData: LiveData<Story> = _storyData

    private val _snackbarText = MutableLiveData<String>()
    val snackbarText: LiveData<String> = _snackbarText

    companion object {
        private const val TAG = "StoryDetailViewModel"
    }

    fun getStoryDetail(id: String) {
        _isLoading.postValue(true)

        viewModelScope.launch(Dispatchers.Default) {
            try {
                val result = storyRepository.getStoryDetail(storyId = id)
                _storyData.postValue(result)
                _isLoading.postValue(false)
            } catch (e: Exception) {
                _isLoading.postValue(false)
                Log.e(TAG, "getStoryDetail: ${e.message}")
                _snackbarText.postValue("Failed fetch detail")
            }
        }
    }
}