package com.example.storyapp.repository

import com.example.storyapp.data.model.request.LoginRequest
import com.example.storyapp.data.model.request.RegisterRequest
import com.example.storyapp.data.services.local.UserPreference
import com.example.storyapp.data.services.remote.ApiServices
import kotlinx.coroutines.Dispatchers

class UserRepository private constructor(
    private val apiServices: ApiServices,
    private val pref: UserPreference
) {

    suspend fun login(email: String, password: String): String? {
        with(Dispatchers.IO) {
            try {
                val request = LoginRequest(
                    email = email,
                    password = password
                )
                val response = apiServices.login(request)

                if (!response.isSuccessful) {
                    throw Exception(response.body()?.message.toString())
                } else {

                    pref.saveToken(response.body()?.loginResult?.token)

                    return pref.getToken()
                }
            } catch (e: Exception) {
                throw Exception(e.message.toString())
            }
        }
    }

    suspend fun getUserToken(): String? {
        with(Dispatchers.IO) {
            try {
                val userToken = pref.getToken()
                return userToken
            } catch (e: Exception) {
                throw Exception(e.message.toString())
            }
        }
    }

    suspend fun register(name: String, email: String, password: String): Boolean {
        with(Dispatchers.IO) {
            try {
                val request = RegisterRequest(
                    name = name,
                    email = email,
                    password = password
                )
                val response = apiServices.register(request)

                if (!response.isSuccessful) {
                    throw Exception(response.body()?.message)
                } else {
                    return response.body()?.error == false
                }

            } catch (e: Throwable) {
                throw Exception(e.message.toString())
            }
        }
    }

    suspend fun logout(): Boolean {
        with(Dispatchers.IO) {
            try {
                pref.updateToken(token = null)
                return true
            } catch (e: Throwable) {
                throw Exception(e.message.toString())
            }
        }
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(apiServices: ApiServices, pref: UserPreference): UserRepository {
            if (instance == null) {
                synchronized(this) {
                    instance = UserRepository(apiServices, pref)
                }
            }
            return instance as UserRepository
        }
    }
}