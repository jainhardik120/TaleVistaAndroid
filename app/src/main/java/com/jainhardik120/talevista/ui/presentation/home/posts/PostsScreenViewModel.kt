package com.jainhardik120.talevista.ui.presentation.home.posts

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.jainhardik120.talevista.data.remote.dto.Post
import com.jainhardik120.talevista.domain.repository.PostsRepository
import com.jainhardik120.talevista.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostsScreenViewModel @Inject constructor(private val postsRepository: PostsRepository) :
    ViewModel() {
    var state by mutableStateOf(PostsScreenState())

    val postsPagingFlow = postsRepository.getPosts().cachedIn(viewModelScope)

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            Log.d("TAG", "sendUiEvent: ")
            _uiEvent.send(event)
        }
    }

//    init {
//        viewModelScope.launch {
//            when (val result = postsRepository.getPosts()) {
//                is Resource.Error -> {
//                    sendUiEvent(UiEvent.ShowSnackbar(result.message ?: "Unknown Error"))
//                }
//
//                is Resource.Success -> {
//                    state = state.copy(posts = result.data?.posts ?: emptyList())
//                }
//            }
//        }
//    }

    fun onEvent(event: PostsScreenEvent) {
        when (event) {
            is PostsScreenEvent.DislikeButtonClicked -> {
                viewModelScope.launch {
                    postsRepository.dislikePost(event.postId)
                }
            }

            is PostsScreenEvent.LikeButtonClicked -> {
                viewModelScope.launch {
                    postsRepository.likePost(event.postId)
                }
            }
        }
    }
}

data class PostsScreenState(
    val posts: List<Post> = emptyList()
)

sealed class PostsScreenEvent {
    data class LikeButtonClicked(val postId: String) : PostsScreenEvent()
    data class DislikeButtonClicked(val postId: String) : PostsScreenEvent()
}