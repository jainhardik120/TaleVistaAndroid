package com.jainhardik120.talevista.data.remote

import android.content.SharedPreferences
import com.jainhardik120.talevista.data.remote.dto.CategoriesItem
import com.jainhardik120.talevista.data.remote.dto.CommentsItem
import com.jainhardik120.talevista.data.remote.dto.CreatePostResponse
import com.jainhardik120.talevista.data.remote.dto.MessageResponse
import com.jainhardik120.talevista.data.remote.dto.Posts
import okhttp3.Interceptor
import okhttp3.RequestBody
import okhttp3.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

class TokenInterceptor(
    private val sharedPreferences: SharedPreferences
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = sharedPreferences.getString("TOKEN", "null")
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
        return chain.proceed(request)
    }
}

interface PostsApi {
    companion object{
        const val BASE_URL = "https://tale-vista-server.onrender.com/api/posts/"
    }

    @GET("categories")
    suspend fun getCategories() : List<CategoriesItem>

    @GET(BASE_URL)
    suspend fun getPosts(@Query("page") page: Int? = null, @Query("limit") limit: Int? = null) : Posts

    @GET("category/{category}")
    suspend fun getCategoryPosts(
        @Path("category") category: String,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): Posts

    @POST(BASE_URL)
    suspend fun createPost(
        @Body post: RequestBody
    ): CreatePostResponse

    @PUT("{postId}/like")
    suspend fun likePost(
        @Path("postId") postId: String
    ): MessageResponse

    @PUT("{postId}/dislike")
    suspend fun dislikePost(
        @Path("postId") postId: String
    ): MessageResponse

    @DELETE("{postId}/like")
    suspend fun unlikePost(
        @Path("postId") postId: String
    ): MessageResponse

    @DELETE("{postId}/dislike")
    suspend fun undislikePost(
        @Path("postId") postId: String
    ): MessageResponse

    @GET("{postId}/comments")
    suspend fun getPostComments(
        @Path("postId") postId: String
    ): List<CommentsItem>

    @POST("{postId}/comment")
    suspend fun createComment(
        @Path("postId") postId: String,
        @Body comment: RequestBody
    ): CreatePostResponse

    @PUT("comment/{commentId}/like")
    suspend fun likeComment(
        @Path("commentId") commentId: String
    ): MessageResponse

    @PUT("comment/{commentId}/dislike")
    suspend fun dislikeComment(
        @Path("commentId") commentId: String
    ): MessageResponse

    @DELETE("comment/{commentId}/like")
    suspend fun unlikeComment(
        @Path("commentId") commentId: String
    ): MessageResponse

    @DELETE("comment/{commentId}/dislike")
    suspend fun undislikeComment(
        @Path("commentId") commentId: String
    ): MessageResponse
}