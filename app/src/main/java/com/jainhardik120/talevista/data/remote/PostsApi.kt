package com.jainhardik120.talevista.data.remote

import com.jainhardik120.talevista.data.remote.dto.CategoriesItem
import com.jainhardik120.talevista.data.remote.dto.Comments
import com.jainhardik120.talevista.data.remote.dto.CreatePostResponse
import com.jainhardik120.talevista.data.remote.dto.MessageResponse
import com.jainhardik120.talevista.data.remote.dto.Posts
import com.jainhardik120.talevista.data.remote.dto.SinglePost
import com.jainhardik120.talevista.util.BASE_SERVER_URL
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface PostsApi {
    companion object{
        const val BASE_URL = "$BASE_SERVER_URL/api/posts/"
    }

    @GET("categories")
    suspend fun getCategories(): Response<List<CategoriesItem>>

    @GET(BASE_URL)
    suspend fun getPosts(
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null,
        @Query("userId") userId: String? = null,
        @Query("category") category: String? = null,
    ): Posts

    @GET("post/{postId}")
    suspend fun getSinglePost(
        @Path("postId") postId: String
    ): Response<SinglePost>

    @DELETE("post/{postId}")
    suspend fun deletePost(
        @Path("postId") postId: String
    ): Response<MessageResponse>

    @PUT("post/{postId}")
    suspend fun editPost(
        @Path("postId") postId: String,
        @Body post: RequestBody
    ): Response<MessageResponse>

    @POST(BASE_URL)
    suspend fun createPost(
        @Body post: RequestBody
    ): Response<CreatePostResponse>

    @PUT("{postId}/like")
    suspend fun likePost(
        @Path("postId") postId: String
    ): Response<MessageResponse>

    @PUT("{postId}/dislike")
    suspend fun dislikePost(
        @Path("postId") postId: String
    ): Response<MessageResponse>

    @DELETE("{postId}/like")
    suspend fun unlikePost(
        @Path("postId") postId: String
    ): Response<MessageResponse>

    @DELETE("{postId}/dislike")
    suspend fun undislikePost(
        @Path("postId") postId: String
    ): Response<MessageResponse>

    @GET("{postId}/comments")
    suspend fun getPostComments(
        @Path("postId") postId: String,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null,
    ): Comments

    @POST("{postId}/comment")
    suspend fun createComment(
        @Path("postId") postId: String,
        @Body comment: RequestBody
    ): Response<CreatePostResponse>

    @PUT("comment/{commentId}/like")
    suspend fun likeComment(
        @Path("commentId") commentId: String
    ): Response<MessageResponse>

    @PUT("comment/{commentId}/dislike")
    suspend fun dislikeComment(
        @Path("commentId") commentId: String
    ): Response<MessageResponse>

    @DELETE("comment/{commentId}/like")
    suspend fun unlikeComment(
        @Path("commentId") commentId: String
    ): Response<MessageResponse>

    @DELETE("comment/{commentId}/dislike")
    suspend fun undislikeComment(
        @Path("commentId") commentId: String
    ): Response<MessageResponse>

}