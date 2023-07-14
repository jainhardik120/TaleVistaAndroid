package com.jainhardik120.talevista.ui.presentation.home.postscreen

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jainhardik120.talevista.data.remote.dto.Author
import com.jainhardik120.talevista.data.remote.dto.CommentsItem
import com.jainhardik120.talevista.domain.repository.AuthController
import com.jainhardik120.talevista.domain.repository.PostsRepository
import com.jainhardik120.talevista.domain.repository.UserPreferences
import com.jainhardik120.talevista.ui.presentation.home.HomeScreenRoutes
import com.jainhardik120.talevista.ui.presentation.home.posts.ListState
import com.jainhardik120.talevista.util.NAVIGATE_UP_ROUTE
import com.jainhardik120.talevista.util.Resource
import com.jainhardik120.talevista.util.UiEvent
import com.jainhardik120.talevista.util.timeAgoText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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


    private val _comments = MutableStateFlow<List<CommentsItem>>(emptyList())
    val comments: StateFlow<List<CommentsItem>> get() = _comments.asStateFlow()

    private var page by mutableStateOf(1)
    var canPaginate by mutableStateOf(false)
    var listState by mutableStateOf(ListState.IDLE)


    fun getComments() = viewModelScope.launch {
        if (page == 1 || (page != 1 && canPaginate) && listState == ListState.IDLE) {
            listState = if (page == 1) ListState.LOADING else ListState.PAGINATING
            postsRepository.getPostComments(postId, page).collect { it ->
                if (it.currentPage != 0) {
                    canPaginate = it.currentPage < it.totalPages
                    if (page == 1) {
                        _comments.value = it.comments.map { comment ->
                            comment.copy(createdAt = timeAgoText(comment.createdAt))
                        }
                    } else {
                        _comments.value = _comments.value + it.comments.map { comment ->
                            comment.copy(createdAt = timeAgoText(comment.createdAt))
                        }
                    }
                    listState = ListState.IDLE
                    if (canPaginate) {
                        page++
                    }
                } else {
                    listState = if (page == 1) ListState.ERROR else ListState.PAGINATION_EXHAUST
                }
            }
        }
    }

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
        getComments()
        state =
            state.copy(selfUserPicture = authController.getUserInfo(UserPreferences.PICTURE) ?: "")
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
                    }, onSuccess = {
                        _comments.value = listOf(
                            CommentsItem(
                                _id = it._id,
                                author = Author(
                                    authController.getUserId() ?: "",
                                    "you",
                                    authController.getUserInfo(UserPreferences.PICTURE) ?: ""
                                ),
                                createdAt = "0 seconds ago",
                                detail = state.newCommentContent,
                                dislikesCount = 0,
                                likedByCurrentUser = false,
                                likesCount = 0,
                                dislikedByCurrentUser = false,
                                post = state.post?.post?._id ?: "",
                                updatedAt = "",
                                __v = 0
                            )
                        ) + _comments.value
                        state = state.copy(newCommentContent = "")
                    })
                }
            }

            is PostScreenEvent.NewCommentChanged -> {
                state = state.copy(newCommentContent = event.string)
            }

            is PostScreenEvent.DeletePostButtonClicked -> {
                handleRepositoryResponse({ postsRepository.deletePost(postId) },
                    { sendUiEvent(UiEvent.Navigate(NAVIGATE_UP_ROUTE)) })
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

            PostScreenEvent.EditPostButtonClicked -> {
                sendUiEvent(
                    UiEvent.Navigate(
                        HomeScreenRoutes.CreatePostScreen.route + "?postId=" + state.post?.post?._id
                    )
                )
            }
        }

    }
}

