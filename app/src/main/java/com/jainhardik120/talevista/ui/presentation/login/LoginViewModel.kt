package com.jainhardik120.talevista.ui.presentation.login

import android.app.Activity
import android.content.Context
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
import com.jainhardik120.talevista.domain.repository.AuthController
import com.jainhardik120.talevista.ui.presentation.Screen
import com.jainhardik120.talevista.util.Resource
import com.jainhardik120.talevista.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authController: AuthController
) : ViewModel() {

    private var googleIdToken = ""

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
            when (val loginResult = authController.loginWithEmailPassword(emailAddress, password)) {
                is Resource.Error -> {
                    sendUiEvent(UiEvent.ShowSnackbar(loginResult.message ?: "Unknown Error"))
                }

                is Resource.Success -> {
                    sendUiEvent(UiEvent.Navigate(Screen.HomeScreen.route))
                }
            }
        }
    }

    private fun launchOneTapIntent(
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
            oneTapClient.beginSignIn(signInRequest).addOnSuccessListener { result ->
                try {
                    val intentSenderRequest =
                        IntentSenderRequest.Builder(result.pendingIntent).build()
                    launcher.launch(intentSenderRequest)
                } catch (e: Exception) {
                    sendUiEvent(UiEvent.ShowSnackbar(e.message ?: ""))
                }
            }.addOnFailureListener { e ->
                sendUiEvent(UiEvent.ShowSnackbar(e.message ?: ""))
            }
        }
    }

    fun handleIntentResult(result: ActivityResult) {
        if (result.resultCode != Activity.RESULT_OK) {
            return
        }
        val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
        val idToken = credential.googleIdToken
        if (idToken != null) {
            viewModelScope.launch {
                when (val response = authController.useGoogleIdToken(idToken)) {
                    is Resource.Error -> {
                        sendUiEvent(UiEvent.ShowSnackbar(response.message ?: "Unknown Error"))
                    }

                    is Resource.Success -> {
                        if (response.data?.first == true) {
                            sendUiEvent(UiEvent.Navigate(Screen.HomeScreen.route))
                        } else {
                            if (response.data?.second != null) {
                                val userInfo = response.data.second
                                googleIdToken = idToken
                                if (userInfo != null) {
                                    state = state.copy(
                                        firstName = userInfo.firstName,
                                        lastName = userInfo.lastName,
                                        picture = userInfo.picture
                                    )
                                    sendUiEvent(UiEvent.Navigate(LoginScreenRoutes.GoogleUsernameScreen.route))
                                }
                            }
                        }
                    }
                }
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

            is LoginEvent.RegisterMailChanged -> {
                state = state.copy(registerEmail = event.email)
            }

            is LoginEvent.RegisterPasswordChanged -> {
                state = state.copy(registerPassword = event.password)
            }

            is LoginEvent.RegisterUserNameChanged -> {
                state = state.copy(registerUsername = event.username, showUsernames = false)
            }

            is LoginEvent.LoginButtonClicked -> {
                onLoginClicked(state.loginEmail, state.loginPassword)
            }

            is LoginEvent.LaunchOneTapClient -> {
                launchOneTapIntent(event.context, event.launcher)
            }

            is LoginEvent.SignUpTextClicked -> {
                sendUiEvent(UiEvent.Navigate(LoginScreenRoutes.RegisterMailScreen.route))
            }

            is LoginEvent.RegisterMailButtonClicked -> {
                viewModelScope.launch {
                    when (val response = authController.checkEmail(state.registerEmail)) {
                        is Resource.Error -> {
                            sendUiEvent(UiEvent.ShowSnackbar(response.message ?: "Unknown Error"))
                        }

                        is Resource.Success -> {
                            if (response.data == true) {
                                sendUiEvent(UiEvent.Navigate(LoginScreenRoutes.RegisterPasswordScreen.route))
                            } else {
                                sendUiEvent(UiEvent.ShowSnackbar("This email is already signed up"))
                            }
                        }
                    }
                }
            }

            is LoginEvent.RegisterPasswordButtonClicked -> {
                // TODO : Validate Password Length and Characters
                sendUiEvent(UiEvent.Navigate(LoginScreenRoutes.RegisterUsernameScreen.route))
            }

            is LoginEvent.RegisterUsernameButtonClicked -> {
                viewModelScope.launch {
                    when (val availableUserNameResponse =
                        authController.checkUsername(state.registerUsername)) {
                        is Resource.Error -> {
                            sendUiEvent(
                                UiEvent.ShowSnackbar(
                                    availableUserNameResponse.message ?: "Unknown Error"
                                )
                            )
                        }

                        is Resource.Success -> {
                            if (availableUserNameResponse.data?.first == true) {
                                if (event.google) {
                                    when (val createAccountResponse =
                                        authController.createNewFromGoogleIdToken(
                                            googleIdToken,
                                            state.registerUsername,
                                            state.firstName,
                                            state.lastName,
                                            state.dob,
                                            state.picture,
                                            state.gender
                                        )) {
                                        is Resource.Error -> {
                                            sendUiEvent(
                                                UiEvent.ShowSnackbar(
                                                    createAccountResponse.message ?: "Unknown Error"
                                                )
                                            )
                                        }

                                        is Resource.Success -> {
                                            sendUiEvent(UiEvent.Navigate(Screen.HomeScreen.route))
                                        }
                                    }
                                } else {

                                    when (val createAccountResponse = authController.createUser(
                                        state.registerEmail,
                                        state.registerPassword,
                                        state.registerUsername,
                                        state.firstName,
                                        state.lastName,
                                        state.dob,
                                        state.picture,
                                        state.gender
                                    )) {
                                        is Resource.Error -> {
                                            sendUiEvent(
                                                UiEvent.ShowSnackbar(
                                                    createAccountResponse.message ?: "Unknown Error"
                                                )
                                            )
                                        }

                                        is Resource.Success -> {
                                            sendUiEvent(UiEvent.Navigate(Screen.HomeScreen.route))
                                        }
                                    }
                                }

                            } else {
                                state = state.copy(
                                    recommendedUserNames = availableUserNameResponse.data?.second
                                        ?: emptyList(), showUsernames = true
                                )
                                sendUiEvent(UiEvent.ShowSnackbar("Sorry, this username is taken"))
                            }
                        }
                    }
                }

            }
        }
    }
}
