package com.jainhardik120.talevista.data.repository

import android.content.SharedPreferences
import com.jainhardik120.talevista.data.remote.TaleVistaApi
import com.jainhardik120.talevista.domain.repository.AuthController
import com.jainhardik120.talevista.util.Resource
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthControllerImpl @Inject constructor(
    private val api: TaleVistaApi,
    private val sharedPreferences: SharedPreferences
) : AuthController {

    companion object {
        private const val TAG = "AuthController"
        private const val TOKEN_KEY = "TOKEN"
        private const val USER_ID_KEY = "USER_ID"
    }

    private fun storeToken(token : String){
        with(sharedPreferences.edit()){
            putString(TOKEN_KEY, token)
        }.apply()
    }
    private fun storeUserId(userId : String){
        with(sharedPreferences.edit()){
            putString(USER_ID_KEY, userId)
        }.apply()
    }

    private fun RequestBody(vararg pairs: Pair<String, String>): RequestBody {
        return JSONObject(pairs.toMap()).toString()
            .toRequestBody("application/json".toMediaTypeOrNull())
    }


    override fun isLoggedIn(): Boolean {
        val token = sharedPreferences.getString(TOKEN_KEY, "null")
        return (token != null && token != "null")
    }

    override fun getToken(): String? {
        return if (isLoggedIn()) {
            sharedPreferences.getString(TOKEN_KEY, "null")
        } else {
            null
        }
    }

    override fun getUserId(): String? {
        return if (isLoggedIn()) {
            sharedPreferences.getString(USER_ID_KEY, "null")
        } else {
            null
        }
    }

    override suspend fun loginWithEmailPassword(email: String, password: String): Resource<String> {
        return try {
            val loginResponse = api.loginUser(
                RequestBody(
                    Pair("email", email),
                    Pair("password", password)
                )
            )
            if (loginResponse.isSuccessful) {
                storeToken(loginResponse.body()?.token?:"null")
                storeUserId(loginResponse.body()?.userId?:"null")
                Resource.Success(loginResponse.body()?.userId)
            } else {
                val errorBody = loginResponse.errorBody()?.string()
                val jsonBody = errorBody?.let { JSONObject(it) }
                if (jsonBody != null) {
                    Resource.Error(message = jsonBody.getString("error"))
                } else {
                    Resource.Error(message = "Unknown Error")
                }
            }
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "Unknown Error")
        }
    }

    override suspend fun checkEmail(email: String): Resource<Boolean> {
        return try {
            val response = api.checkEmail(
                RequestBody(
                    Pair("email", email)
                )
            )
            if (response.isSuccessful) {
                Resource.Success(true)
            } else {
                Resource.Success(false)
            }
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "Unknown Error")
        }
    }


    override suspend fun checkUsername(username: String): Resource<Pair<Boolean, List<String>>> {
        return try {
            val response = api.checkUsername(
                RequestBody(
                    Pair("username", username)
                )
            )
            if (response.isSuccessful) {
                Resource.Success(Pair(true, emptyList()))
            } else {
                if (response.code() == 400) {
                    Resource.Error(message = "Invalid Characters")
                } else {
                    val errorBody = response.errorBody()?.string()
                    val jsonBody = errorBody?.let { JSONObject(it) }
                    if (jsonBody != null) {
                        val list = List(
                            jsonBody.getJSONArray("similarUsernames").length()
                        ) { index -> jsonBody.getJSONArray("similarUsernames")[index].toString() }
                        Resource.Success(Pair(false, list))
                    } else {
                        Resource.Error(message = "Unknown Error")
                    }
                }
            }
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "Unknown Error")
        }
    }

    override suspend fun createUser(
        email: String,
        password: String,
        username: String
    ): Resource<String> {
        return try {
            val loginResponse = api.createUser(
                RequestBody(
                    Pair("email", email),
                    Pair("password", password),
                    Pair("username", username)
                )
            )
            if (loginResponse.isSuccessful) {
                storeToken(loginResponse.body()?.token?:"null")
                storeUserId(loginResponse.body()?.userId?:"null")
                Resource.Success(loginResponse.body()?.userId)
            } else {
                val errorBody = loginResponse.errorBody()?.string()
                val jsonBody = errorBody?.let { JSONObject(it) }
                if (jsonBody != null) {
                    Resource.Error(message = jsonBody.getString("error"))
                } else {
                    Resource.Error(message = "Unknown Error")
                }
            }
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "Unknown Error")
        }
    }

    override suspend fun useGoogleIdToken(idToken: String): Resource<Boolean> {
        return try {
            val loginResponse = api.authorizeGoogleIdToken(
                RequestBody(
                    Pair("idToken", idToken)
                )
            )
            if (loginResponse.isSuccessful) {
                storeToken(loginResponse.body()?.token ?: "null")
                storeUserId(loginResponse.body()?.userId ?: "null")
                Resource.Success(true)
            } else {
                if (loginResponse.code() == 400) {
                    val errorBody = loginResponse.errorBody()?.string()
                    val jsonBody = errorBody?.let { JSONObject(it) }
                    if (jsonBody != null) {
                        if (jsonBody.getString("error") == "Username required") {
                            Resource.Success(false)
                        } else {
                            Resource.Error(message = jsonBody.getString("error"))
                        }
                    } else {
                        Resource.Error(message = "Unknown Error")
                    }
                } else {
                    val errorBody = loginResponse.errorBody()?.string()
                    val jsonBody = errorBody?.let { JSONObject(it) }
                    if (jsonBody != null) {
                        Resource.Error(message = jsonBody.getString("error"))
                    } else {
                        Resource.Error(message = "Unknown Error")
                    }
                }

            }
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "Unknown Error")
        }
    }

    override suspend fun createNewFromGoogleIdToken(
        idToken: String,
        username: String
    ): Resource<Boolean> {
        return try {
            val response = api.createNewFromGoogleIdToken(
                RequestBody(
                    Pair("idToken", idToken),
                    Pair("username", username)
                )
            )
            if (response.isSuccessful) {
                storeToken(response.body()?.token ?: "null")
                storeUserId(response.body()?.userId ?: "null")
                Resource.Success(true)
            } else {
                val errorBody = response.errorBody()?.string()
                val jsonBody = errorBody?.let { JSONObject(it) }
                if (jsonBody != null) {
                    Resource.Error(message = jsonBody.getString("error"))
                } else {
                    Resource.Error(message = "Unknown Error")
                }
            }
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "Unknown Error")
        }
    }
}