package com.jainhardik120.talevista.ui.presentation.home.postscreen

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jainhardik120.talevista.domain.repository.AuthController
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
class PostViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val postsRepository: PostsRepository,
    private val authController: AuthController
) : ViewModel() {

    var state by mutableStateOf(PostState())
    private var postId = ""


    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            Log.d("TAG", "sendUiEvent: ")
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

    fun init() {
        postId = savedStateHandle.get<String>("postId") ?: return
        handleRepositoryResponse(call = { postsRepository.getSinglePost(postId) }, onSuccess = {
            state = state.copy(
                post = it,
                isAuthorUser = (it.post.author._id == authController.getUserId())
            )
        })
        handleRepositoryResponse(call = { postsRepository.getPostComments(postId) }, onSuccess = {
            state = state.copy(comments = it)
        })
    }

    fun onEvent(event: PostScreenEvent) {
        when (event) {
            PostScreenEvent.CommentPostButtonClicked -> {
                if (state.newCommentContent.isNotBlank()) {
                    handleRepositoryResponse({
                        postsRepository.createComment(
                            postId,
                            state.newCommentContent
                        )
                    })
                }
            }

            is PostScreenEvent.NewCommentChanged -> {
                state = state.copy(newCommentContent = event.string)
            }

            is PostScreenEvent.DeletePostButtonClicked -> {
                handleRepositoryResponse({ postsRepository.deletePost(postId) },
                    { sendUiEvent(UiEvent.Navigate("NAVIGATE_BACK")) })
            }

            PostScreenEvent.PostAuthorClicked -> {
                sendUiEvent(
                    UiEvent.Navigate(
                        HomeScreenRoutes.ProfileScreen.withArgs(
                            state.post?.post?.author?._id ?: ""
                        )
                    )
                )
            }
        }

    }
}

