package com.jainhardik120.talevista.ui.presentation.home.profile

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
fun ProfileScreen(viewModel: ProfileScreenViewModel) {
    LaunchedEffect(key1 = Unit, block = {
        viewModel.init()
    })
    val state = viewModel.state
    if (state.user != null) {
        val user = state.user
        Text(text = "${user.user.first_name} ${user.user.last_name}")
    }
}