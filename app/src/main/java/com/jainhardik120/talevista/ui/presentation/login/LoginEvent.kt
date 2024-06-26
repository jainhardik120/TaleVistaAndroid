package com.jainhardik120.talevista.ui.presentation.login

import android.content.Context
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest

sealed class LoginEvent {
    data class LoginMailChanged(val email: String) : LoginEvent()
    data class ResetMailChanged(val email: String) : LoginEvent()
    data class LoginPasswordChanged(val password: String) : LoginEvent()
    data class RegisterPasswordChanged(val password: String) : LoginEvent()
    data class RegisterUserNameChanged(val username: String) : LoginEvent()
    data class RegisterMailChanged(val email: String) : LoginEvent()
    data class RegisterNameChanged(val name: String) : LoginEvent()
    data class GenderChanged(val gender: Gender) : LoginEvent()
    data class DateOfBirthChanged(val date: Long) : LoginEvent()
    data class ImageUrlChanged(val url: String) : LoginEvent()
    data class LaunchOneTapClient(
        val context: Context,
        val launcher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>
    ) : LoginEvent()

    object LoginButtonClicked : LoginEvent()
    object SignUpTextClicked : LoginEvent()
    object RegisterMailButtonClicked : LoginEvent()
    object RegisterPasswordButtonClicked : LoginEvent()
    object RegisterUsernameButtonClicked : LoginEvent()
    object ForgotPasswordClicked : LoginEvent()
    object SendResetMailClicked : LoginEvent()
}


