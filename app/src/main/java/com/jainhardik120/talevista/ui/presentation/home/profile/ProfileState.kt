package com.jainhardik120.talevista.ui.presentation.home.profile

data class ProfileState(
    val createdAt: String = "",
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val username: String = "",
    val verified: Boolean = false
)