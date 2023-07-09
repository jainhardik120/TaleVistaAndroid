package com.jainhardik120.talevista.data.repository

import android.content.SharedPreferences
import android.util.Log
import com.jainhardik120.talevista.data.remote.TaleVistaApi
import com.jainhardik120.talevista.data.remote.dto.LoginResponse
import com.jainhardik120.talevista.domain.repository.AuthController
import com.jainhardik120.talevista.domain.repository.USERPREFERENCES
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

    private fun storeUserInfo(loginResponse: LoginResponse?) {
        if (loginResponse != null) {
            with(sharedPreferences.edit()) {
                putString(USER_ID_KEY, loginResponse.userId)
                putString(USERPREFERENCES.EMAIL.key, loginResponse.email)
                putString(USERPREFERENCES.FIRST_NAME.key, loginResponse.firstName)
                putString(USERPREFERENCES.LAST_NAME.key, loginResponse.lastName)
                putString(USERPREFERENCES.PICTURE.key, loginResponse.picture)
                putString(TOKEN_KEY, loginResponse.token)
            }.apply()
        }
    }

    private fun RequestBody(vararg pairs: Pair<String, String>): RequestBody {
        return JSONObject(pairs.toMap()).toString()
            .toRequestBody("application/json".toMediaTypeOrNull())
    }


    override fun isLoggedIn(): Boolean {
        val token = sharedPreferences.getString(TOKEN_KEY, null)
        return (token != null && token != "null")
    }

    override fun getToken(): String? {
        return if (isLoggedIn()) {
            sharedPreferences.getString(TOKEN_KEY, null)
        } else {
            null
        }
    }

    override fun getUserId(): String? {
        return if (isLoggedIn()) {
            sharedPreferences.getString(USER_ID_KEY, null)
        } else {
            null
        }
    }

    override fun getUserInfo(key: USERPREFERENCES): String? {
        return if (isLoggedIn()) {
            sharedPreferences.getString(key.key, null)
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
                storeUserInfo(loginResponse.body())
                Resource.Success(loginResponse.body()?.userId)
            } else {
                Log.d(TAG, "loginWithEmailPassword: Entered Login Response Error Body")
                val errorBody = loginResponse.errorBody()?.string()
                val jsonBody = errorBody?.let { JSONObject(it) }
                if (jsonBody != null) {
                    Resource.Error(message = jsonBody.getString("error"))
                } else {
                    Resource.Error(message = "Unknown Error")
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "loginWithEmailPassword: ${e.printStackTrace()}")
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
        username: String,
        firstName: String,
        lastName: String,
        dob: String,
        picture: String,
        gender: String
    ): Resource<String> {
        return try {
            val loginResponse = api.createUser(
                RequestBody(
                    Pair("email", email),
                    Pair("password", password),
                    Pair("username", username),
                    Pair("first_name", firstName),
                    Pair("last_name", lastName),
                    Pair("picture", picture),
                    Pair("dob", dob),
                    Pair("gender", gender),

                    )
            )
            if (loginResponse.isSuccessful) {
                storeUserInfo(loginResponse.body())
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

    data class GoogleNewUserResponse(
        val firstName: String,
        val lastName: String,
        val picture: String
    )

    override suspend fun useGoogleIdToken(idToken: String): Resource<Pair<Boolean, GoogleNewUserResponse?>> {
        return try {
            val loginResponse = api.authorizeGoogleIdToken(
                RequestBody(
                    Pair("idToken", idToken)
                )
            )
            if (loginResponse.isSuccessful) {
                storeUserInfo(loginResponse.body())
                Resource.Success(Pair(true, null))
            } else {
                if (loginResponse.code() == 400) {
                    val errorBody = loginResponse.errorBody()?.string()
                    val jsonBody = errorBody?.let { JSONObject(it) }
                    if (jsonBody != null) {
                        if (jsonBody.getString("error") == "Username Required") {
                            Resource.Success(
                                Pair(
                                    false, GoogleNewUserResponse(
                                        jsonBody.getString("first_name"),
                                        jsonBody.getString("last_name"),
                                        jsonBody.getString("picture"),
                                    )
                                )
                            )
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
        username: String,
        firstName: String,
        lastName: String,
        dob: String,
        picture: String,
        gender: String
    ): Resource<Boolean> {
        return try {
            val response = api.createNewFromGoogleIdToken(
                RequestBody(
                    Pair("idToken", idToken),
                    Pair("username", username),
                    Pair("first_name", firstName),
                    Pair("last_name", lastName),
                    Pair("picture", picture),
                    Pair("dob", dob),
                    Pair("gender", gender),
                )
            )
            if (response.isSuccessful) {
                storeUserInfo(response.body())
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