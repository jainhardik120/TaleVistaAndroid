package com.jainhardik120.talevista.data.remote

import com.jainhardik120.talevista.data.remote.dto.Posts
import com.jainhardik120.talevista.data.remote.dto.User
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface UsersApi {
    companion object {
        const val BASE_URL = "https://tale-vista-server.onrender.com/api/user/"
    }

    @GET(BASE_URL)
    suspend fun getSelfUserDetails(): Response<User>

    @GET("username/{username}")
    suspend fun getByUserName(@Path("username") username: String): Response<User>

    @GET("userId/{userId}")
    suspend fun getByUserId(@Path("userId") userId: String): Response<User>

    @GET("userId/{userId}/likes")
    suspend fun getLikedPosts(
        @Path("userId") userId: String,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): Posts

}