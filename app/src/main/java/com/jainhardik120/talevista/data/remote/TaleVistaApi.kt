package com.jainhardik120.talevista.data.remote

import com.jainhardik120.talevista.data.remote.dto.LoginResponse
import com.jainhardik120.talevista.data.remote.dto.MessageResponse
import com.jainhardik120.talevista.util.BASE_SERVER_URL
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TaleVistaApi {

    companion object {
        const val BASE_URL = "$BASE_SERVER_URL/api/auth/"
    }

    @POST("login")
    suspend fun loginUser(@Body body: RequestBody): Response<LoginResponse>

    @POST("checkEmail")
    suspend fun checkEmail(@Body body: RequestBody): Response<MessageResponse>

    @POST("checkUsername")
    suspend fun checkUsername(@Body body: RequestBody): Response<MessageResponse>

    @POST("createUser")
    suspend fun createUser(@Body body: RequestBody): Response<LoginResponse>

    @POST("useGoogleIdToken")
    suspend fun authorizeGoogleIdToken(@Body body: RequestBody): Response<LoginResponse>

    @POST("createNewFromGoogleIdToken")
    suspend fun createNewFromGoogleIdToken(@Body body: RequestBody): Response<LoginResponse>

    @GET("sendVerificationMail/{userId}")
    suspend fun sendVerificationMail(@Path("userId") userId: String): Response<MessageResponse>
}