package com.jainhardik120.talevista.data.repository

import android.content.SharedPreferences
import android.util.Log
import com.jainhardik120.talevista.data.remote.TaleVistaApi
import com.jainhardik120.talevista.data.remote.dto.LoginResponse
import com.jainhardik120.talevista.domain.repository.AuthController
import com.jainhardik120.talevista.domain.repository.UserPreferences
import com.jainhardik120.talevista.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
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

    private fun <T> ResponseBody?.toResource(
        onBody: (JSONObject) -> Resource<T> = {
            Log.d(TAG, "toResource: ${it.getString("error")}")
            Resource.Error(message = it.getString("error"))
        }
    ): Resource<T> {
        val errorBody = this?.string()
        val jsonBody = errorBody?.let { JSONObject(it) }
        return if (jsonBody != null) {
            onBody(jsonBody)
        } else {
            Log.d(TAG, "toResource: Unknown Error")
            Resource.Error(message = "Unknown Error")
        }
    }

    private suspend fun <T, R> handleApiCall(
        call: suspend () -> Response<T>,
        onSuccess: (T) -> Resource<R>,
        onError: (ResponseBody?, Int) -> Resource<R> = { error, _ ->
            error.toResource()
        }
    ): Resource<R> {
        return try {
            withContext(Dispatchers.IO) {
                val response = call.invoke()
                if (response.isSuccessful) {
                    response.body()?.let {
                        onSuccess(it)
                    } ?: Resource.Error("Response body is null")
                } else {
                    onError(response.errorBody(), response.code())
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "handleApiCall: ${e.message}")
            Resource.Error(e.message ?: "Unknown Error")
        }
    }

    private fun storeUserInfo(loginResponse: LoginResponse?) {
        if (loginResponse != null) {
            with(sharedPreferences.edit()) {
                putString(USER_ID_KEY, loginResponse.userId)
                putString(UserPreferences.EMAIL.key, loginResponse.email)
                putString(UserPreferences.FIRST_NAME.key, loginResponse.firstName)
                putString(UserPreferences.LAST_NAME.key, loginResponse.lastName)
                putString(UserPreferences.PICTURE.key, loginResponse.picture)
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

    override fun getUserInfo(key: UserPreferences): String? {
        return if (isLoggedIn()) {
            sharedPreferences.getString(key.key, null)
        } else {
            null
        }
    }

    override suspend fun loginWithEmailPassword(email: String, password: String): Resource<String> {
        return handleApiCall(call = {
            api.loginUser(
                RequestBody(
                    Pair("email", email),
                    Pair("password", password)
                )
            )
        }, onSuccess = {
            storeUserInfo(it)
            Resource.Success(it.userId)
        })
    }

    override suspend fun checkEmail(email: String): Resource<Boolean> {
        return handleApiCall(call = {
            api.checkEmail(
                RequestBody(
                    Pair("email", email)
                )
            )
        }, onSuccess = {
            Resource.Success(true)
        }, onError = { _, _ ->
            Resource.Success(false)
        })
    }


    override suspend fun checkUsername(username: String): Resource<Pair<Boolean, List<String>>> {
        return handleApiCall(call = {
            api.checkUsername(
                RequestBody(
                    Pair("username", username)
                )
            )
        }, onSuccess = {
            Resource.Success(Pair(true, emptyList()))
        }, onError = { body, code ->
            if (code == 400) {
                Resource.Error(message = "Invalid Characters")
            } else {
                body.toResource { jsonBody ->
                    val list = List(
                        jsonBody.getJSONArray("similarUsernames").length()
                    ) { index -> jsonBody.getJSONArray("similarUsernames")[index].toString() }
                    Resource.Success(Pair(false, list))
                }
            }
        })
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
        return handleApiCall(call = {
            api.createUser(
                RequestBody(
                    Pair("email", email),
                    Pair("password", password),
                    Pair("username", username),
                    Pair("first_name", firstName),
                    Pair("last_name", lastName),
                    Pair("picture", picture),
                    Pair("dob", dob),
                    Pair("gender", gender)
                )
            )
        }, onSuccess = {
            storeUserInfo(it)
            Resource.Success(it.userId)
        })
    }

    data class GoogleNewUserResponse(
        val firstName: String,
        val lastName: String,
        val picture: String
    )

    override suspend fun useGoogleIdToken(idToken: String): Resource<Pair<Boolean, GoogleNewUserResponse?>> {
        return handleApiCall(call = {
            api.authorizeGoogleIdToken(
                RequestBody(
                    Pair("idToken", idToken)
                )
            )
        }, onSuccess = {
            storeUserInfo(it)
            Resource.Success(Pair(true, null))
        }, onError = { body, code ->
            if (code == 400) {
                body.toResource { jsonBody ->
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
                }
            } else {
                body.toResource()
            }
        })
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
        return handleApiCall(call = {
            api.createNewFromGoogleIdToken(
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
        }, onSuccess = {
            storeUserInfo(it)
            Resource.Success(true)
        })
    }
}