package com.jainhardik120.talevista.domain.repository

import com.jainhardik120.talevista.data.remote.PostsQuery
import com.jainhardik120.talevista.data.remote.dto.CategoriesItem
import com.jainhardik120.talevista.data.remote.dto.Comments
import com.jainhardik120.talevista.data.remote.dto.CreatePostResponse
import com.jainhardik120.talevista.data.remote.dto.MessageResponse
import com.jainhardik120.talevista.data.remote.dto.Posts
import com.jainhardik120.talevista.data.remote.dto.SinglePost
import com.jainhardik120.talevista.util.Resource
import kotlinx.coroutines.flow.Flow

interface PostsRepository {

    suspend fun getCategories(): Resource<List<CategoriesItem>>

    suspend fun getPostsCustom(page: Int, query: PostsQuery = PostsQuery()): Flow<Posts>

    suspend fun createPost(content: String, category: String): Resource<CreatePostResponse>

    suspend fun editPost(
        postId: String,
        content: String,
        category: String
    ): Resource<MessageResponse>

    suspend fun likePost(postId: String): Resource<MessageResponse>

    suspend fun dislikePost(postId: String): Resource<MessageResponse>

    suspend fun unlikePost(postId: String): Resource<MessageResponse>

    suspend fun undislikePost(postId: String): Resource<MessageResponse>

    suspend fun getPostComments(postId: String, page: Int): Flow<Comments>

    suspend fun createComment(postId: String, comment: String): Resource<CreatePostResponse>

    suspend fun likeComment(commentId: String): Resource<MessageResponse>

    suspend fun dislikeComment(commentId: String): Resource<MessageResponse>

    suspend fun unlikeComment(commentId: String): Resource<MessageResponse>

    suspend fun undislikeComment(commentId: String): Resource<MessageResponse>

    suspend fun getSinglePost(postId: String): Resource<SinglePost>

    suspend fun deletePost(postId: String): Resource<MessageResponse>
}