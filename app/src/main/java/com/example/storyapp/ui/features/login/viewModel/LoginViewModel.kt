package com.example.storyapp.ui.features.login.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository): ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isAuthenticated = MutableLiveData<Boolean>()
    val isAuthenticated: LiveData<Boolean> = _isAuthenticated

    private val _snackbarText = MutableLiveData<String>()
    val snackbarText: LiveData<String> = _snackbarText

    companion object {
        private const val TAG = "LoginViewModel"
    }

    fun login(email: String, password: String) {
        _isLoading.postValue(true)

        viewModelScope.launch(Dispatchers.Default) {
            try {
                val result = userRepository.login(email, password)
                _isLoading.postValue(false)
                if (result?.isNotEmpty() == true) {
                    _isAuthenticated.postValue(true)
                } else {
                    _isLoading.postValue(false)
                    _isAuthenticated.postValue(false)
                    _snackbarText.postValue("Email or Password not registered")
                }
            } catch (e: Throwable) {
                _isLoading.postValue(false)
                _isAuthenticated.postValue(false)
                Log.e(TAG, "login: ${e.message}")
                _snackbarText.postValue(e.message.toString())
            }
        }
    }
}