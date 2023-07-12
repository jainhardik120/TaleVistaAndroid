package com.jainhardik120.talevista.domain.repository

import com.jainhardik120.talevista.data.remote.dto.MessageResponse
import com.jainhardik120.talevista.data.repository.AuthControllerImpl
import com.jainhardik120.talevista.util.Resource

interface AuthController {
    fun isLoggedIn(): Boolean
    fun getToken(): String?

    fun getUserId(): String?

    fun getUserInfo(key: UserPreferences): String?

    fun logOutCurrentUser()

    suspend fun loginWithEmailPassword(email: String, password: String): Resource<String>

    suspend fun checkEmail(email: String): Resource<Boolean>

    suspend fun checkUsername(username: String): Resource<Pair<Boolean, List<String>>>

    suspend fun createUser(
        email: String,
        password: String,
        username: String,
        firstName: String,
        lastName: String,
        dob: String,
        picture: String,
        gender: String
    ): Resource<String>

    suspend fun useGoogleIdToken(idToken: String): Resource<Pair<Boolean, AuthControllerImpl.GoogleNewUserResponse?>>

    suspend fun createNewFromGoogleIdToken(
        idToken: String,
        username: String,
        firstName: String,
        lastName: String,
        dob: String,
        picture: String,
        gender: String
    ): Resource<Boolean>

    suspend fun sendResetMail(
        email: String
    ): Resource<String>

    suspend fun resetPassword(
        token: String,
        newPassword: String
    ): Resource<MessageResponse>
}

