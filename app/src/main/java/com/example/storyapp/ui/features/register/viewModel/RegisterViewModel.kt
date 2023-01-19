package com.example.storyapp.ui.features.register.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterViewModel(private val userRepository: UserRepository): ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isRegistered = MutableLiveData<Boolean>()
    val isRegistered: LiveData<Boolean> = _isRegistered

    private val _snackbarText = MutableLiveData<String>()
    val snackbarText: LiveData<String> = _snackbarText

    companion object {
        private const val TAG = "RegisterViewModel"
    }

    fun register(name: String, email: String, password: String) {
        _isLoading.postValue(true)

        viewModelScope.launch(Dispatchers.Default) {
            try {
                val result = userRepository.register(name, email, password)
                _isLoading.postValue(false)
                if (result) {
                    _isRegistered.postValue(true)
                    _snackbarText.postValue("Account Successfully Registered")
                } else {
                    _isRegistered.postValue(false)
                    _snackbarText.postValue("Register Fail")
                }
            } catch (e: Throwable) {
                _isLoading.postValue(false)
                _isRegistered.postValue(false)
                Log.e(TAG, "register: ${e.message}")
                _snackbarText.postValue(e.message.toString())
            }
        }
    }
}