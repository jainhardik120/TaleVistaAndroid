package com.jainhardik120.talevista.util

sealed class UiEvent {
    data class Navigate(val route: String) : UiEvent()
    data class ShowSnackbar(
        val message: String,
        val action: String? = null
    ) : UiEvent()
}

const val NAVIGATE_UP_ROUTE = "NAVIGATE_BACK"

const val NAVIGATE_LOGIN_ROUTE = "LOGIN_ROUTE"