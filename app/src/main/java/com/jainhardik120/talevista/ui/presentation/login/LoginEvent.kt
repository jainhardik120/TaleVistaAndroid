package com.jainhardik120.talevista.ui.presentation.login

sealed class LoginEvent{
    data class LoginMailChanged(val email: String) : LoginEvent()
    data class LoginPasswordChanged(val password: String) : LoginEvent()
    data class RegisterFNameChanged(val name: String) : LoginEvent()
    data class RegisterLNameChanged(val name: String) : LoginEvent()
    data class RegisterPasswordChanged(val password: String) : LoginEvent()
    data class RegisterRePasswordChanged(val password: String) : LoginEvent()
    data class RegisterMailChanged(val email: String) : LoginEvent()
    object LoginButtonClicked:LoginEvent()
    object RegisterButtonClicked:LoginEvent()
    object ChangeScreen:LoginEvent()

}
