package com.jainhardik120.talevista.ui.presentation.home.profile

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.jainhardik120.talevista.data.remote.PostsQuery
import com.jainhardik120.talevista.data.remote.UsersApi
import com.jainhardik120.talevista.domain.repository.AuthController
import com.jainhardik120.talevista.domain.repository.PostsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileScreenViewModel @Inject constructor(
    private val usersApi: UsersApi,
    private val authController: AuthController,
    private val postsRepository: PostsRepository
) : ViewModel() {

    var state by mutableStateOf(ProfileState())

    val postsPagingFlow = postsRepository.getPosts(PostsQuery(userId = authController.getUserId()))
        .cachedIn(viewModelScope)

    init {
        viewModelScope.launch {
            try {
                val response = usersApi.getSelfDetails()
                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) {
                        state = state.copy(
                            createdAt = user.createdAt,
                            email = user.email,
                            firstName = user.first_name,
                            lastName = user.last_name,
                            username = user.username,
                            verified = user.verified
                        )
                    }
                }
            } catch (e: Exception) {
                Log.d("TAG", "${e.message}")
            }
        }
    }
}

