package com.jainhardik120.talevista.ui.presentation.home.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

const val SELF_USER = "self_user"

@Composable
fun ProfileScreen(viewModel: ProfileScreenViewModel) {
    val state = viewModel.state
    Column() {
        Text(text = state.username)
        Text(text = state.firstName)
        Text(text = state.lastName)
        Text(text = state.email)
        Text(text = state.createdAt)
    }
//    PostsContainer(posts = viewModel.postsPagingFlow.collectAsLazyPagingItems())
}