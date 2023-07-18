package com.jainhardik120.talevista.ui.presentation.login

import com.jainhardik120.talevista.util.BASE_SERVER_URL

data class LoginState(
    val loginEmail: String = "",
    val loginPassword: String = "",
    val registerEmail: String = "",
    val registerPassword: String = "",
    val recommendedUserNames: List<String> = emptyList(),
    val showUsernames: Boolean = false,
    val name: String = "",
    val picture: String = "$BASE_SERVER_URL/avatar8).png",
    val dob: Long = 0,
    val gender: Gender? = null,
    val googleIdToken: String = "",
    val isGoogleUsed: Boolean = false,
    val resetMail: String = "",
    val loginButtonEnabled: Boolean = false,
    val registerEmailButtonEnabled: Boolean = false,
    val registerPasswordButtonEnabled: Boolean = false
)
