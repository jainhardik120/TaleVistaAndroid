package com.jainhardik120.talevista.ui.presentation.login

data class LoginState(
    val loginEmail: String = "",
    val loginPassword: String = "",
    val registerEmail: String = "",
    val registerPassword: String = "",
    val registerUsername: String = "",
    val currPage: LoginPage = LoginPage.ExistingAccount,
    val recommendedUserNames: List<String> = emptyList(),
    val showUsernames: Boolean = false,
    val firstName: String = "",
    val lastName: String = "",
    val picture: String = "",
    val dob: String = "",
    val gender: String = ""
)

enum class LoginPage{
    ExistingAccount,
    NewAccount
}