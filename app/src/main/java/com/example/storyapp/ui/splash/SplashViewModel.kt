package com.example.storyapp.ui.splash

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.repository.UserRepository
import kotlinx.coroutines.launch

class SplashViewModel(private val userRepository: UserRepository): ViewModel() {
    private val _isAuth = MutableLiveData<Boolean>()
    val isAuth: LiveData<Boolean> = _isAuth

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _snackbarText = MutableLiveData<String>()
    val snackbarText: LiveData<String> = _snackbarText

    companion object {
        private const val TAG = "SplashViewModel"
    }

    fun checkAuthToken() {
        _isLoading.postValue(true)

        viewModelScope.launch {
            try {
                val token = userRepository.getUserToken()
                _isLoading.postValue(false)
                if (!token.isNullOrEmpty()) {
                    _isAuth.postValue(true)
                } else {
                    _isAuth.postValue(false)
                }
            } catch (e: Throwable) {
                Log.e(TAG, "checkAuthToken: ${e.message}")
                _snackbarText.postValue(e.message.toString())
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}