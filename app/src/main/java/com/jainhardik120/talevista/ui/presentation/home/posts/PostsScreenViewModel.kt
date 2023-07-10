package com.jainhardik120.talevista.ui.presentation.home.posts

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jainhardik120.talevista.data.remote.PostsQuery
import com.jainhardik120.talevista.data.remote.dto.Post
import com.jainhardik120.talevista.domain.repository.AuthController
import com.jainhardik120.talevista.domain.repository.PostsRepository
import com.jainhardik120.talevista.domain.repository.UserPreferences
import com.jainhardik120.talevista.ui.components.PostCardEvent
import com.jainhardik120.talevista.ui.presentation.home.HomeScreenRoutes
import com.jainhardik120.talevista.util.Resource
import com.jainhardik120.talevista.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostsScreenViewModel @Inject constructor(
    private val postsRepository: PostsRepository,
    private val authController: AuthController
) :
    ViewModel() {

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    var state by mutableStateOf(PostsScreenState())

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> get() = _posts.asStateFlow()

    private var page by mutableStateOf(1)
    var canPaginate by mutableStateOf(false)
    var listState by mutableStateOf(ListState.IDLE)

    init {
        state =
            state.copy(profileImageUrl = authController.getUserInfo(UserPreferences.PICTURE) ?: "")
    }

    fun getPosts() = viewModelScope.launch {
        if (page == 1 || (page != 1 && canPaginate) && listState == ListState.IDLE) {
            listState = if (page == 1) ListState.LOADING else ListState.PAGINATING
            postsRepository.getPostsCustom(page, PostsQuery()).collect() {
                if (it.currentPage != 0) {
                    canPaginate = it.currentPage < it.totalPages
                    if (page == 1) {
                        _posts.value = it.posts
                    } else {
                        _posts.value = _posts.value + it.posts
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
                        Log.d("TAG", "handleRepositoryResponse: Successful Execution")
                        onSuccess(response.data)
                    }
                }
            }
        }
    }

    private fun updatePostLikeDislikeAt(
        index: Int = 0,
        like: Boolean = false,
        dislike: Boolean = false
    ) {
        val updatedPosts = _posts.value.toMutableList()
        updatedPosts[index] = updatedPosts[index].copy(
            dislikedByCurrentUser = dislike, likedByCurrentUser = like
        )
        _posts.value = updatedPosts
    }

    private fun onPostCardEvent(event: PostCardEvent, post: Post, index: Int) {
        when (event) {
            PostCardEvent.AuthorClicked -> {
                sendUiEvent(UiEvent.Navigate(HomeScreenRoutes.ProfileScreen.withArgs(post.author._id)))
            }

            PostCardEvent.DislikeButtonClicked -> {
                val postId = _posts.value[index]._id
                if (_posts.value[index].dislikedByCurrentUser) {
                    handleRepositoryResponse({ postsRepository.undislikePost(postId) }) {
                        updatePostLikeDislikeAt(index, dislike = false)
                    }
                } else {
                    handleRepositoryResponse({ postsRepository.dislikePost(postId) }) {
                        updatePostLikeDislikeAt(index, dislike = true)
                    }
                }
            }

            PostCardEvent.LikeButtonClicked -> {
                val postId = post._id
                if (_posts.value[index].likedByCurrentUser) {
                    handleRepositoryResponse({ postsRepository.unlikePost(postId) }) {
                        updatePostLikeDislikeAt(index, like = false)
                    }
                } else {
                    handleRepositoryResponse({ postsRepository.likePost(postId) }) {
                        updatePostLikeDislikeAt(index, like = true)
                    }
                }
            }

            PostCardEvent.PostClicked -> {
                sendUiEvent(UiEvent.Navigate(HomeScreenRoutes.SinglePostScreen.withArgs(post._id)))
            }
        }
    }

    fun onEvent(event: PostsScreenEvent) {
        when (event) {
            PostsScreenEvent.ProfileLogoClicked -> {
                sendUiEvent(
                    UiEvent.Navigate(
                        HomeScreenRoutes.ProfileScreen.withArgs(
                            authController.getUserId() ?: ""
                        )
                    )
                )
            }

            is PostsScreenEvent.CardEvent -> {
                onPostCardEvent(event.event, event.post, event.index)
            }
        }
    }
}

data class PostsScreenState(
    val profileImageUrl: String = ""
)

enum class ListState {
    IDLE,
    LOADING,
    PAGINATING,
    ERROR,
    PAGINATION_EXHAUST,
}