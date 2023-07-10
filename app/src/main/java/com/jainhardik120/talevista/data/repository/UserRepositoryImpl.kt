package com.jainhardik120.talevista.data.repository

import android.util.Log
import com.jainhardik120.talevista.data.remote.UsersApi
import com.jainhardik120.talevista.data.remote.dto.User
import com.jainhardik120.talevista.domain.repository.UserRepository
import com.jainhardik120.talevista.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val api: UsersApi
) : UserRepository {

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
            withContext(Dispatchers.IO) {
                val response = call.invoke()
                response.toResource()
            }
        } catch (e: Exception) {
            Log.d(TAG, "handleApiCall: ${e.message}")
            Resource.Error(e.message ?: "Unknown Error")
        }
    }

    override suspend fun getSelfUserDetails(): Resource<User> {
        return handleApiCall { api.getSelfUserDetails() }
    }

    override suspend fun getByUsername(username: String): Resource<User> {
        return handleApiCall { api.getByUserName(username) }
    }

    override suspend fun getByUserId(userId: String): Resource<User> {
        return handleApiCall { api.getByUserId(userId) }
    }
}