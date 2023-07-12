package com.jainhardik120.talevista.ui.presentation.login

import android.app.Activity
import android.content.Context
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
import com.jainhardik120.talevista.domain.repository.AuthController
import com.jainhardik120.talevista.ui.presentation.Screen
import com.jainhardik120.talevista.util.Resource
import com.jainhardik120.talevista.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authController: AuthController
) : ViewModel() {
    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _username = MutableStateFlow("")
    val username = _username.asStateFlow()

    var state by mutableStateOf(LoginState())

    private val _usernameAvailable = MutableStateFlow(true)

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val usernameAvailable = username.debounce(500).flatMapLatest { it ->
        if (it.isBlank()) {
            flowOf(true)
        } else {
            flow {
                val response = authController.checkUsername(it)
                if (response is Resource.Success) {
                    if (response.data?.first == true) {
                        emit(true)
                    } else {
                        emit(false)
                    }
                } else {
                    emit(false)
                }
            }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        _usernameAvailable.value
    )

    private lateinit var oneTapClient: SignInClient

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    private fun <T> handleRepositoryResponse(
        call: suspend () -> Resource<T>, onSuccess: (T) -> Unit = {}, onError: (String?) -> Unit = {
            sendUiEvent(UiEvent.ShowSnackbar(message = it ?: "Unknown Error"))
        }
    ) {
        viewModelScope.launch {
            when (val response = call()) {
                is Resource.Error -> {
                    onError(response.message)
                }

                is Resource.Success -> {
                    if (response.data != null) {
                        onSuccess(response.data)
                    }
                }
            }
        }
    }


    private fun onLoginClicked(emailAddress: String, password: String) {
        handleRepositoryResponse(call = {
            authController.loginWithEmailPassword(emailAddress, password)
        }, onSuccess = {
            sendUiEvent(UiEvent.Navigate(Screen.HomeScreen.route))
        })
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
            handleRepositoryResponse(call = {
                authController.useGoogleIdToken(idToken)
            }, onSuccess = { data ->
                if (data.first) {
                    sendUiEvent(UiEvent.Navigate(Screen.HomeScreen.route))
                } else {
                    if (data.second != null) {
                        val userInfo = data.second
                        state = state.copy(googleIdToken = idToken, isGoogleUsed = true)
                        if (userInfo != null) {
                            state = state.copy(
                                firstName = userInfo.firstName,
                                lastName = userInfo.lastName
                            )
                            sendUiEvent(UiEvent.Navigate(LoginScreenRoutes.RegisterUsernameScreen.route))
                        }
                    }
                }
            })
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
                _username.value = event.username
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
                if (state.registerEmail.isNotEmpty()) {
                    handleRepositoryResponse(call = {
                        authController.checkEmail(state.registerEmail)
                    }, onSuccess = {
                        if (it) {
                            sendUiEvent(UiEvent.Navigate(LoginScreenRoutes.RegisterPasswordScreen.route))
                        } else {
                            sendUiEvent(UiEvent.ShowSnackbar("This email is already signed up"))
                        }
                    })
                } else {
                    sendUiEvent(UiEvent.ShowSnackbar("Email empty"))
                }

            }

            is LoginEvent.RegisterPasswordButtonClicked -> {
                if (state.registerPassword.isNotEmpty() && state.registerPassword.length >= 8) {
                    state = state.copy(isGoogleUsed = false)
                    sendUiEvent(UiEvent.Navigate(LoginScreenRoutes.RegisterUsernameScreen.route))
                } else {
                    sendUiEvent(UiEvent.ShowSnackbar("Minimum length for password is 8"))
                }
            }

            is LoginEvent.RegisterUsernameButtonClicked -> {
                handleRepositoryResponse(call = {
                    authController.checkUsername(_username.value)
                }, onSuccess = {
                    if (it.first) {
                        if (state.isGoogleUsed) {
                            handleRepositoryResponse(
                                call = {
                                    authController.createNewFromGoogleIdToken(
                                        state.googleIdToken,
                                        _username.value,
                                        state.firstName,
                                        state.lastName,
                                        dobString(state.dob),
                                        state.picture,
                                        state.gender.codeName
                                    )
                                },
                                onSuccess = { sendUiEvent(UiEvent.Navigate(Screen.HomeScreen.route)) })
                        } else {
                            handleRepositoryResponse(call = {
                                authController.createUser(
                                    state.registerEmail,
                                    state.registerPassword,
                                    _username.value,
                                    state.firstName,
                                    state.lastName,
                                    dobString(state.dob),
                                    state.picture,
                                    state.gender.codeName
                                )
                            }, onSuccess = {
                                sendUiEvent(UiEvent.Navigate(Screen.HomeScreen.route))
                            })
                        }
                    } else {
                        state = state.copy(
                            recommendedUserNames = it.second, showUsernames = true
                        )
                        _usernameAvailable.value = false
                    }
                })
            }

            is LoginEvent.GenderChanged -> {
                state = state.copy(gender = event.gender)
            }

            is LoginEvent.RegisterFNameChanged -> {
                state = state.copy(firstName = event.name)
            }

            is LoginEvent.RegisterLNameChanged -> {
                state = state.copy(lastName = event.name)
            }

            is LoginEvent.DateOfBirthChanged -> {
                state = state.copy(dob = event.date)
            }

            LoginEvent.ForgotPasswordClicked -> {
                sendUiEvent(UiEvent.Navigate(LoginScreenRoutes.ForgotPasswordScreen.route))
            }
        }
    }

    private fun dobString(milliseconds: Long): String {
        val date = Date(milliseconds)
        Log.d("TAG", "dobString: $milliseconds")
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        sdf.timeZone = TimeZone.getTimeZone("UTC")

        return sdf.format(date)
    }

}
