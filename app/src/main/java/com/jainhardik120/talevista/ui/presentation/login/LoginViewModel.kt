package com.jainhardik120.talevista.ui.presentation.login

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.jainhardik120.talevista.data.remote.TaleVistaApi
import com.jainhardik120.talevista.domain.repository.AuthController
import com.jainhardik120.talevista.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val authController: AuthController
) : ViewModel() {

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    var state by mutableStateOf(LoginState())

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    private lateinit var oneTapClient: SignInClient

    private fun onLoginClicked(emailAddress: String, password: String) {
        viewModelScope.launch {
            try {
//                val body = JSONObject(
//                    mapOf(
//                        Pair("email", emailAddress),
//                        Pair("password", password)
//                    )
//                ).toString().toRequestBody(
//                    "application/json".toMediaTypeOrNull()
//                )
//                Log.d("TAG", "onLoginClicked: $body")
//                val loginResult = JSONObject(
//                    api.loginUser(
//                        body
//                    )
//                )
//                handleAuthorizationResponse(loginResult)
                val loginResult = authController.loginWithEmailPassword(emailAddress, password)
            }  catch (e: Exception) {
                Log.d("TAG", "onLoginClicked: ${e.message}")
            }
        }
    }

    private fun registerUser(
        registerFName: String,
        registerLName: String,
        registerEmail: String,
        registerPassword: String,
        registerRePassword: String
    ) {
        viewModelScope.launch {
            try {
//                val registerResult = JSONObject(
////                    api.registerUser(
////                        JSONObject(
////                            mapOf(
////                                Pair("email", registerEmail),
////                                Pair("first_name", registerFName),
////                                Pair("last_name", registerLName),
////                                Pair("password", registerPassword),
////                                Pair("repassword", registerRePassword)
////                            )
////                        ).toString().toRequestBody("application/json".toMediaTypeOrNull())
////                    )
//                )
//                handleAuthorizationResponse(registerResult)
            } catch (e: Exception) {

            }

        }
    }

    fun launchOneTapIntent(
        context: Context,
        launcher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>
    ) {
        viewModelScope.launch {
            oneTapClient = Identity.getSignInClient(context)
            val signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(
                    BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId("274460086707-a2ls3orip5kgmt9403975li88iqo5dcr.apps.googleusercontent.com")
                        .setFilterByAuthorizedAccounts(false)
                        .build()
                )
                .setAutoSelectEnabled(true)
                .build()
            try {
                val result = oneTapClient.beginSignIn(signInRequest).await()
                val intentSenderRequest =
                    IntentSenderRequest.Builder(result.pendingIntent).build()
                launcher.launch(intentSenderRequest)
            } catch (e: Exception) {
                sendUiEvent(UiEvent.ShowSnackbar(e.message ?: ""))
            }
        }
    }

    private fun handleAuthorizationResponse(response: JSONObject) {
        Log.d("TAG", "handleAuthorizationResponse: $response")
        with(sharedPreferences.edit()) {
            putString("TOKEN", response.getString("token"))
            putString("F_NAME", response.getJSONObject("user").getString("first_name"))
            putString("L_NAME", response.getJSONObject("user").getString("last_name"))
            putString("EMAIL", response.getJSONObject("user").getString("email"))
            putBoolean("VERIFIED", response.getJSONObject("user").getBoolean("verified"))
            putString("PICTURE", response.getJSONObject("user").getString("picture"))
        }.apply()
    }

    fun handleIntentResult(result: ActivityResult) {
        if (result.resultCode != Activity.RESULT_OK) {
            return
        }
        val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
        val idToken = credential.googleIdToken
        if (idToken != null) {
            viewModelScope.launch {
//                try {
//                    val loginResult = JSONObject(
//                        api.authorizeGoogleIdToken(
//                            JSONObject(
//                                mapOf(
//                                    Pair("idToken", idToken)
//                                )
//                            ).toString().toRequestBody("application/json".toMediaTypeOrNull())
//                        )
//                    )
//                    handleAuthorizationResponse(loginResult)
//
//                } catch (e: Exception) {
//                    sendUiEvent(UiEvent.ShowSnackbar(e.message ?: ""))
//                }
            }
        } else {
            sendUiEvent(UiEvent.ShowSnackbar("Can't Login with Google"))
        }
    }

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.LoginMailChanged -> {
                state = state.copy(loginEmail = event.email)
            }

            is LoginEvent.LoginPasswordChanged -> {
                state = state.copy(loginPassword = event.password)
            }

            is LoginEvent.RegisterFNameChanged -> {
                state = state.copy(registerFName = event.name)
            }

            is LoginEvent.RegisterLNameChanged -> {
                state = state.copy(registerLName = event.name)
            }

            is LoginEvent.RegisterMailChanged -> {
                state = state.copy(registerEmail = event.email)
            }

            is LoginEvent.RegisterPasswordChanged -> {
                state = state.copy(registerPassword = event.password)
            }

            is LoginEvent.RegisterRePasswordChanged -> {
                state = state.copy(registerRePassword = event.password)
            }

            is LoginEvent.LoginButtonClicked -> {
                onLoginClicked(state.loginEmail, state.loginPassword)
            }

            is LoginEvent.RegisterButtonClicked -> {
                registerUser(
                    state.registerFName,
                    state.registerLName,
                    state.registerEmail,
                    state.registerPassword,
                    state.registerPassword
                )
            }

            LoginEvent.ChangeScreen -> {
                state = if (state.currPage == LoginPage.ExistingAccount) {
                    state.copy(currPage = LoginPage.NewAccount)
                } else {
                    state.copy(currPage = LoginPage.ExistingAccount)
                }
            }
        }
    }
}
