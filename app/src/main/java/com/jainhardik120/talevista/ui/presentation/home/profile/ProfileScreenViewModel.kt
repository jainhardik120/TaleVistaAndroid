package com.jainhardik120.talevista.ui.presentation.home.profile

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jainhardik120.talevista.data.remote.PostsQuery
import com.jainhardik120.talevista.data.remote.dto.Post
import com.jainhardik120.talevista.domain.repository.AuthController
import com.jainhardik120.talevista.domain.repository.PostsRepository
import com.jainhardik120.talevista.domain.repository.UserRepository
import com.jainhardik120.talevista.ui.components.PostCardEvent
import com.jainhardik120.talevista.ui.presentation.home.HomeScreenRoutes
import com.jainhardik120.talevista.ui.presentation.home.posts.ListState
import com.jainhardik120.talevista.util.NAVIGATE_LOGIN_ROUTE
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
class ProfileScreenViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val authController: AuthController,
    private val usersRepository: UserRepository,
    private val postsRepository: PostsRepository
) : ViewModel() {

    var state by mutableStateOf(ProfileState())

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            Log.d("TAG", "sendUiEvent: ")
            _uiEvent.send(event)
        }
    }


    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> get() = _posts.asStateFlow()

    private var page by mutableStateOf(1)
    var canPaginate by mutableStateOf(false)
    var listState by mutableStateOf(ListState.IDLE)


    private val _likedPosts = MutableStateFlow<List<Post>>(emptyList())
    val likedPosts: StateFlow<List<Post>> get() = _likedPosts.asStateFlow()

    private var likedPage by mutableStateOf(1)
    var likedCanPaginate by mutableStateOf(false)
    var likedListState by mutableStateOf(ListState.IDLE)

    fun getPosts() = viewModelScope.launch {
        if ((page == 1 || (page != 1 && canPaginate) && listState == ListState.IDLE) && state.userId.isNotEmpty()) {
            listState = if (page == 1) ListState.LOADING else ListState.PAGINATING
            postsRepository.getPostsCustom(page, PostsQuery(userId = state.userId)).collect() {
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

    fun getLikedPosts() = viewModelScope.launch {
        if ((likedPage == 1 || (likedPage != 1 && likedCanPaginate) && likedListState == ListState.IDLE) && state.userId.isNotEmpty()) {
            likedListState = if (likedPage == 1) ListState.LOADING else ListState.PAGINATING
            usersRepository.getPostsLikedByUser(state.userId, likedPage).collect() {
                if (it.currentPage != 0) {
                    likedCanPaginate = it.currentPage < it.totalPages
                    if (likedPage == 1) {
                        _likedPosts.value = it.posts
                    } else {
                        _likedPosts.value = _likedPosts.value + it.posts
                    }
                    likedListState = ListState.IDLE
                    if (likedCanPaginate) {
                        likedPage++
                    }
                } else {
                    likedListState =
                        if (likedPage == 1) ListState.ERROR else ListState.PAGINATION_EXHAUST
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

    fun init() {
        val userId = savedStateHandle.get<String>("userId") ?: return
        val selfId = authController.getUserId() ?: return
        state = state.copy(userId = userId, isSelfUser = (userId == selfId))
        loadUser()
        getPosts()
        getLikedPosts()
    }

    private fun loadUser() {
        if (state.userId.isEmpty() && !state.isSelfUser) {
            return
        }
        handleRepositoryResponse(call = {
            if (state.isSelfUser) {
                usersRepository.getSelfUserDetails()
            } else {
                usersRepository.getByUserId(state.userId)
            }
        }, onSuccess = {
            state = state.copy(user = it)
        })
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
//                sendUiEvent(UiEvent.Navigate(HomeScreenRoutes.ProfileScreen.withArgs(post.author._id)))
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

    fun onEvent(event: ProfileScreenEvent) {
        when (event) {

            is ProfileScreenEvent.CardEvent -> {
                onPostCardEvent(event.event, event.post, event.index)
            }

            ProfileScreenEvent.MoreIconClicked -> {
                state = state.copy(menuExpanded = !state.menuExpanded)
            }

            ProfileScreenEvent.DismissMenu -> {
                state = state.copy(menuExpanded = false)
            }

            ProfileScreenEvent.LogoutItemClicked -> {
                authController.logOutCurrentUser()
                sendUiEvent(UiEvent.Navigate(NAVIGATE_LOGIN_ROUTE))
            }
        }
    }

}

sealed class ProfileScreenEvent {
    data class CardEvent(val event: PostCardEvent, val post: Post, val index: Int) :
        ProfileScreenEvent()

    object MoreIconClicked : ProfileScreenEvent()
    object DismissMenu : ProfileScreenEvent()
    object LogoutItemClicked : ProfileScreenEvent()

}
