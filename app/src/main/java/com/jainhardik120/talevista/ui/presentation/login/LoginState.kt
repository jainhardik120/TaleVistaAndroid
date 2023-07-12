package com.jainhardik120.talevista.ui.presentation.login

data class LoginState(
    val loginEmail: String = "",
    val loginPassword: String = "",
    val registerEmail: String = "",
    val registerPassword: String = "",
    val recommendedUserNames: List<String> = emptyList(),
    val showUsernames: Boolean = false,
    val firstName: String = "",
    val lastName: String = "",
    val picture: String = "",
    val dob: Long = 0,
    val gender: Gender = Gender.MALE,
    val googleIdToken: String = "",
    val isGoogleUsed: Boolean = false
)
