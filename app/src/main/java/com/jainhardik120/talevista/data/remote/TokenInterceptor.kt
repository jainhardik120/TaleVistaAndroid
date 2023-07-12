package com.jainhardik120.talevista.data.remote

import android.content.SharedPreferences
import okhttp3.Interceptor

class TokenInterceptor(
    private val sharedPreferences: SharedPreferences
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val token = sharedPreferences.getString("TOKEN", "null")
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
        return chain.proceed(request)
    }
}

