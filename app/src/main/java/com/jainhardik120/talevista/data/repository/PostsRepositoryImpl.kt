package com.jainhardik120.talevista.data.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.jainhardik120.talevista.data.remote.PostsApi
import com.jainhardik120.talevista.data.remote.PostsPagingSource
import com.jainhardik120.talevista.data.remote.dto.CategoriesItem
import com.jainhardik120.talevista.data.remote.dto.CommentsItem
import com.jainhardik120.talevista.data.remote.dto.CreatePostResponse
import com.jainhardik120.talevista.data.remote.dto.MessageResponse
import com.jainhardik120.talevista.data.remote.dto.Post
import com.jainhardik120.talevista.data.remote.dto.Posts
import com.jainhardik120.talevista.domain.repository.PostsRepository
import com.jainhardik120.talevista.util.Resource
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class PostsRepositoryImpl @Inject constructor(
    private val api: PostsApi
) : PostsRepository {

    companion object {
        private const val TAG = "PostsRepository"
    }

    private fun RequestBody(vararg pairs: Pair<String, String>): RequestBody {
        return JSONObject(pairs.toMap()).toString()
            .toRequestBody("application/json".toMediaTypeOrNull())
    }

    private fun <T> Response<T>.toResource(): Resource<T> {
        return if (isSuccessful) {
            body()?.let {
                Resource.Success(it)
            } ?: Resource.Error("Response body is null")
        } else {
            val errorBody = errorBody()?.string()
            val jsonBody = errorBody?.let { JSONObject(it) }
            if (jsonBody != null) {
                Log.d(TAG, "toResource: ${jsonBody.getString("error")}")
                Resource.Error(message = jsonBody.getString("error"))
            } else {
                Log.d(TAG, "toResource: Unknown Error")
                Resource.Error(message = "Unknown Error")
            }
        }
    }

    private suspend fun <T> handleApiCall(call: suspend () -> Response<T>): Resource<T> {
        return try {
            val response = call.invoke()
            response.toResource()
        } catch (e: Exception) {
            Log.d(TAG, "handleApiCall: ${e.message}")
            Resource.Error(e.message ?: "Unknown Error")
        }
    }

    override suspend fun getCategories(): Resource<List<CategoriesItem>> {
        return handleApiCall { api.getCategories() }
    }

    override suspend fun getPosts(page: Int?, limit: Int?): Resource<Posts> {
        return handleApiCall { api.getPostsResponse(page = page, limit = limit) }
    }

    override fun getPosts(): Flow<PagingData<Post>> {
        return Pager(
            config = PagingConfig(
                pageSize = 4,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                PostsPagingSource(postsApi = api)
            }
        ).flow
    }

    override suspend fun getCategoryPosts(
        category: String,
        page: Int?,
        limit: Int?
    ): Resource<Posts> {
        return handleApiCall {
            api.getCategoryPosts(
                category = category,
                page = page,
                limit = limit
            )
        }
    }

    override suspend fun createPost(
        content: String,
        category: String
    ): Resource<CreatePostResponse> {
        return handleApiCall {
            api.createPost(
                RequestBody(
                    Pair("content", content),
                    Pair("category", category)
                )
            )
        }
    }

    override suspend fun likePost(postId: String): Resource<MessageResponse> {
        return handleApiCall { api.likePost(postId) }
    }

    override suspend fun dislikePost(postId: String): Resource<MessageResponse> {
        return handleApiCall { api.dislikePost(postId) }
    }

    override suspend fun unlikePost(postId: String): Resource<MessageResponse> {
        return handleApiCall { api.unlikePost(postId) }
    }

    override suspend fun undislikePost(postId: String): Resource<MessageResponse> {
        return handleApiCall { api.undislikePost(postId) }
    }

    override suspend fun getPostComments(postId: String): Resource<List<CommentsItem>> {
        return handleApiCall { api.getPostComments(postId) }
    }

    override suspend fun createComment(
        postId: String,
        comment: String
    ): Resource<CreatePostResponse> {
        return handleApiCall { api.createComment(postId, RequestBody(Pair("detail", comment))) }
    }

    override suspend fun likeComment(commentId: String): Resource<MessageResponse> {
        return handleApiCall { api.likeComment(commentId) }
    }

    override suspend fun dislikeComment(commentId: String): Resource<MessageResponse> {
        return handleApiCall { api.dislikeComment(commentId) }
    }

    override suspend fun unlikeComment(commentId: String): Resource<MessageResponse> {
        return handleApiCall { api.unlikeComment(commentId) }
    }

    override suspend fun undislikeComment(commentId: String): Resource<MessageResponse> {
        return handleApiCall { api.undislikeComment(commentId) }
    }
}