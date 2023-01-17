package com.example.storyapp.data.services.remote.interceptor

import okhttp3.Interceptor
import okhttp3.Response

class AddHeaderInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer ")
        return chain.proceed(builder.build())
    }
}