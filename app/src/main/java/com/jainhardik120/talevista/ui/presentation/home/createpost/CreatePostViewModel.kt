package com.jainhardik120.talevista.ui.presentation.home.createpost

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jainhardik120.talevista.domain.repository.AuthController
import com.jainhardik120.talevista.domain.repository.PostsRepository
import com.jainhardik120.talevista.util.NAVIGATE_UP_ROUTE
import com.jainhardik120.talevista.util.Resource
import com.jainhardik120.talevista.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class CreatePostViewModel @Inject constructor(
    private val postsRepository: PostsRepository,
    private val savedStateHandle: SavedStateHandle,
    private val authController: AuthController
) : ViewModel() {

    companion object {
        private const val TAG = "CreatePostViewModel"
    }

    var state by mutableStateOf(CreatePostState())

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            Log.d("TAG", "sendUiEvent: ")
            _uiEvent.send(event)
        }
    }

    private fun <T> handleRepositoryResponse(
        call: suspend () -> Resource<T>, onError: (String?) -> Unit = {
            sendUiEvent(UiEvent.ShowSnackbar(message = it ?: "Unknown Error"))
        }, onSuccess: (T) -> Unit = {}
    ) {
        viewModelScope.launch {
            when (val response = call()) {
                is Resource.Error -> {
                    onError(response.message)
                }

                is Resource.Success -> {
                    if (response.data != null) {
                        Log.d(TAG, "handleRepositoryResponse: Successful Execution")
                        onSuccess(response.data)
                    }
                }
            }
        }
    }

    init {
        handleRepositoryResponse(call = { postsRepository.getCategories() },
            onSuccess = { categories ->
                state = state.copy(categories = categories)
                val postId = savedStateHandle.get<String>("postId")
                if (postId != null) {
                    state = state.copy(postId = postId, isNewPost = false)
                    handleRepositoryResponse(call = { postsRepository.getSinglePost(postId) },
                        onSuccess = { post ->
                            if (post.post.author._id != authController.getUserId()) {
                                sendUiEvent(UiEvent.Navigate(NAVIGATE_UP_ROUTE))
                            }
                            val index = categories.indexOfFirst {
                                it.shortName == post.post.category
                            }
                            if (index == -1) {
                                sendUiEvent(UiEvent.Navigate(NAVIGATE_UP_ROUTE))
                            } else {
                                state = state.copy(
                                    postContent = post.post.content, selectedCategory = index
                                )
                            }
                        },
                        onError = {
                            sendUiEvent(UiEvent.Navigate(NAVIGATE_UP_ROUTE))
                        })
                } else {
                    val numCategories = categories.size
                    state = state.copy(selectedCategory = Random.nextInt(until = numCategories))
                }
            },
            onError = {
                sendUiEvent(UiEvent.Navigate(NAVIGATE_UP_ROUTE))
            })
    }

    fun onEvent(event: CreatePostsEvent) {
        when (event) {
            is CreatePostsEvent.CategoryChanged -> {
                state = state.copy(selectedCategory = event.index)
            }

            is CreatePostsEvent.PostContentChanged -> {
                state = state.copy(postContent = event.string)
            }

            CreatePostsEvent.SendButtonClicked -> {
                if (state.postContent.isNotBlank()) {
                    handleRepositoryResponse(call = {
                        if (state.isNewPost) {
                            postsRepository.createPost(
                                content = state.postContent,
                                category = state.categories[state.selectedCategory].shortName
                            )
                        } else {
                            postsRepository.editPost(
                                postId = state.postId,
                                category = state.categories[state.selectedCategory].shortName,
                                content = state.postContent
                            )
                        }
                    }, onSuccess = {
                        sendUiEvent(UiEvent.Navigate(NAVIGATE_UP_ROUTE))
                    })
                }
            }

            CreatePostsEvent.CancelButtonClicked -> {
                if (!(state.isNewPost && state.postContent.isEmpty())) {
                    state = state.copy(isShowingDialog = true)
                } else {
                    sendUiEvent(UiEvent.Navigate(NAVIGATE_UP_ROUTE))
                }
            }

            CreatePostsEvent.DialogConfirmButtonClicked -> {
                state = state.copy(isShowingDialog = false)
                sendUiEvent(UiEvent.Navigate(NAVIGATE_UP_ROUTE))
            }

            CreatePostsEvent.DialogDismissed -> {
                state = state.copy(isShowingDialog = false)
            }
        }
    }
}


