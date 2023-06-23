package com.jainhardik120.talevista.ui.presentation.home.postscreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jainhardik120.talevista.data.remote.dto.SinglePost
import com.jainhardik120.talevista.domain.repository.AuthController
import com.jainhardik120.talevista.domain.repository.PostsRepository
import com.jainhardik120.talevista.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val postsRepository: PostsRepository,
    private val authController: AuthController
) : ViewModel() {

    var state by mutableStateOf(PostState())

    fun init() {
        val postId = savedStateHandle.get<String>("postId") ?: return
        viewModelScope.launch {
            when (val response = postsRepository.getSinglePost(postId)) {
                is Resource.Error -> {

                }

                is Resource.Success -> {
                    state = state.copy(
                        post = response.data,
                        isAuthorUser = (response.data?.post?.author?._id == authController.getUserId())
                    )
                }
            }
        }
    }
}

data class PostState(
    val post: SinglePost? = null,
    val isAuthorUser: Boolean = false
)