package com.example.storyapp.ui.features.stories.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyapp.data.model.Story
import com.example.storyapp.repository.StoryRepository
import com.example.storyapp.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(private val storyRepository: StoryRepository, private val userRepository: UserRepository): ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _logOut = MutableLiveData<Boolean>()
    val logout: LiveData<Boolean> = _logOut

    val stories: LiveData<PagingData<Story>> = storyRepository.getAllStories().cachedIn(viewModelScope)

    private val _snackbarText = MutableLiveData<String>()
    val snackbarText: LiveData<String> = _snackbarText

    companion object {
        private const val TAG = "MainViewModel"
    }

    fun logout() {
        _isLoading.postValue(true)

        viewModelScope.launch(Dispatchers.Default) {
            try {
                val result = userRepository.logout()
                if (result) {
                    _isLoading.postValue(false)
                    _logOut.postValue(true)
                } else {
                    _isLoading.postValue(false)
                    _snackbarText.postValue("Failed remove token")
                }
            } catch (e: Exception) {
                _isLoading.postValue(false)
                Log.e(TAG, "getStories: ${e.message}")
                _snackbarText.postValue("Fetch List Fail")
            }
        }
    }
}