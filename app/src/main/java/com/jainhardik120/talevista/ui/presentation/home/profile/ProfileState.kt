package com.jainhardik120.talevista.ui.presentation.home.profile

import com.jainhardik120.talevista.data.remote.dto.User

data class ProfileState(
    val userId: String = "",
    val isSelfUser: Boolean = false,
    val user: User? = null,
    val selectedTab: Int = 0
)