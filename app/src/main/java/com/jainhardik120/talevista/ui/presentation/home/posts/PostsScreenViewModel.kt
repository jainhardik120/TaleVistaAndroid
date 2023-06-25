package com.jainhardik120.talevista.ui.presentation.home.posts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.jainhardik120.talevista.data.remote.dto.Post
import com.jainhardik120.talevista.domain.repository.PostsRepository
import com.jainhardik120.talevista.ui.presentation.home.HomeScreenRoutes
import com.jainhardik120.talevista.util.Resource
import com.jainhardik120.talevista.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostsScreenViewModel @Inject constructor(private val postsRepository: PostsRepository) :
    ViewModel() {

    val postsPagingFlow = postsRepository.getPosts().cachedIn(viewModelScope)

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    private fun <T> handleRepositoryResponse(
        call: suspend () -> Resource<T>, onSuccess: (T) -> Unit = {}, onError: (String?) -> Unit = {
            sendUiEvent(UiEvent.ShowSnackbar(message = it ?: "Unknown Error"))
        }
    ) {
        viewModelScope.launch {
            when (val response = call()) {
                is Resource.Error -> {
                    onError(response.message)
                }

                is Resource.Success -> {
                    if (response.data != null) {
                        onSuccess(response.data)
                    }
                }
            }
        }
    }

    fun onEvent(event: PostsScreenEvent) {
        when (event) {
            is PostsScreenEvent.DislikeButtonClicked -> {
                handleRepositoryResponse({ postsRepository.dislikePost(event.postId._id) }) {
                    event.postId.dislikedByCurrentUser
                }
            }

            is PostsScreenEvent.LikeButtonClicked -> {
                handleRepositoryResponse({ postsRepository.likePost(event.postId._id) }) {
                    event.postId.likedByCurrentUser = true
                }
            }

            is PostsScreenEvent.PostClicked -> {
                sendUiEvent(UiEvent.Navigate(HomeScreenRoutes.SinglePostScreen.withArgs(event.postId)))
            }
        }
    }
}

sealed class PostsScreenEvent {
    data class LikeButtonClicked(val postId: Post) : PostsScreenEvent()
    data class DislikeButtonClicked(val postId: Post) : PostsScreenEvent()
    data class PostClicked(val postId: String) : PostsScreenEvent()
}