package com.example.storyapp.ui.features.new_story.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.model.request.AddStoryRequest
import com.example.storyapp.repository.StoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class NewStoryViewModel(private val storyRepository: StoryRepository): ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _newStoryUploaded = MutableLiveData<Boolean>()
    val newStoryUploaded: LiveData<Boolean> = _newStoryUploaded

    private val _snackbarText = MutableLiveData<String>()
    val snackbarText: LiveData<String> = _snackbarText

    companion object {
        private const val TAG = "NewStoryViewModel"
    }

    fun addNewStory(description: String, lat: Float?, lon: Float?, photo: File) {
        _isLoading.postValue(true)
        val request = AddStoryRequest(
            description = description,
            lat = lat,
            lon = lon,
        )
        val photoRequestFile = photo.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val photoPart = MultipartBody.Part.createFormData("photo", photo.name,
            photoRequestFile
        )
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val result = storyRepository.addNewStory(storyData = request, photoPart = photoPart)
                if (result) {
                    _isLoading.postValue(false)
                    _newStoryUploaded.postValue(true)
                    _snackbarText.postValue("Successfully Upload New Story")
                } else {
                    _isLoading.postValue(false)
                    _snackbarText.postValue("Upload Story Fail")
                }
            } catch (e: Exception) {
                _isLoading.postValue(false)
                _snackbarText.postValue("${e.message}")
            }
        }
    }
}