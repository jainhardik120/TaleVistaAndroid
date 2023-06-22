package com.jainhardik120.talevista.data.remote

import com.jainhardik120.talevista.data.remote.dto.User
import retrofit2.Response
import retrofit2.http.GET

interface UsersApi {
    companion object {
        const val BASE_URL = "https://tale-vista-server.onrender.com/api/user/"
    }

    @GET(BASE_URL)
    suspend fun getSelfDetails(): Response<User>
}