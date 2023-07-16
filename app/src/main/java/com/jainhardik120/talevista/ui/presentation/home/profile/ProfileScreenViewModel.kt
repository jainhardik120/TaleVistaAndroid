package com.jainhardik120.talevista.ui.presentation.home.profile

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jainhardik120.talevista.data.remote.PostsQuery
import com.jainhardik120.talevista.data.remote.dto.Comment
import com.jainhardik120.talevista.data.remote.dto.Post
import com.jainhardik120.talevista.domain.repository.AuthController
import com.jainhardik120.talevista.domain.repository.PostsRepository
import com.jainhardik120.talevista.domain.repository.UserRepository
import com.jainhardik120.talevista.ui.presentation.home.HomeScreenRoutes
import com.jainhardik120.talevista.ui.presentation.home.posts.ListState
import com.jainhardik120.talevista.util.NAVIGATE_LOGIN_ROUTE
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


    private val _comments = MutableStateFlow<List<ProfileComment>>(emptyList())
    val comments: StateFlow<List<ProfileComment>> get() = _comments.asStateFlow()

    private var commentPage by mutableStateOf(1)
    var commentCanPaginate by mutableStateOf(false)
    var commentListState by mutableStateOf(ListState.IDLE)

    private fun List<Post>.customMapper(): List<Post> {
        return this.map {
            it.copy(createdAt = timeAgoText(it.createdAt))
        }
    }

    fun getPosts() = viewModelScope.launch {
        if ((page == 1 || (page != 1 && canPaginate) && listState == ListState.IDLE) && state.userId.isNotEmpty()) {
            listState = if (page == 1) ListState.LOADING else ListState.PAGINATING
            postsRepository.getPostsCustom(page, PostsQuery(userId = state.userId)).collect() {
                if (it.currentPage != 0) {
                    canPaginate = it.currentPage < it.totalPages
                    if (page == 1) {
                        _posts.value = it.posts.customMapper()
                    } else {
                        _posts.value = _posts.value + it.posts.customMapper()
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
                        _likedPosts.value = it.posts.customMapper()
                    } else {
                        _likedPosts.value = _likedPosts.value + it.posts.customMapper()
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

    fun getComments() = viewModelScope.launch {
        if ((commentPage == 1 || (commentPage != 1 && commentCanPaginate) && commentListState == ListState.IDLE) && state.userId.isNotEmpty()) {
            commentListState = if (commentPage == 1) ListState.LOADING else ListState.PAGINATING
            usersRepository.getCommentsByUser(state.userId, commentPage).collect() { userComments ->
                if (userComments.currentPage != 0) {
                    commentCanPaginate = userComments.currentPage < userComments.totalPages
                    if (commentPage == 1) {
                        _comments.value = userComments.comments.map { it.toProfileComment() }
                    } else {
                        _comments.value =
                            _comments.value + userComments.comments.map { it.toProfileComment() }
                    }
                    commentListState = ListState.IDLE
                    if (commentCanPaginate) {
                        commentPage++
                    }
                } else {
                    commentListState =
                        if (commentPage == 1) ListState.ERROR else ListState.PAGINATION_EXHAUST
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
        getComments()
    }

    private fun Comment.toProfileComment(): ProfileComment {
        return ProfileComment(
            this._id,
            timeAgoText(this.createdAt),
            this.detail,
            this.dislikedByCurrentUser,
            this.dislikesCount,
            this.likedByCurrentUser,
            this.likesCount,
            this.post.content.take(200),
            this.post.author.username,
            this.post.author._id,
            this.post.author.picture,
            this.post._id,
            timeAgoText(this.post.createdAt)
        )
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

    fun onEvent(event: ProfileScreenEvent) {
        when (event) {
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

            is ProfileScreenEvent.PostClicked -> {
                sendUiEvent(UiEvent.Navigate(HomeScreenRoutes.SinglePostScreen.withArgs(event.postId)))
            }

            is ProfileScreenEvent.UserIconClicked -> {
                if (event.userId != state.userId) {
                    sendUiEvent(UiEvent.Navigate(HomeScreenRoutes.ProfileScreen.withArgs(event.userId)))
                }
            }
        }
    }


}

data class ProfileComment(
    val _id: String,
    val createdAt: String,
    val commentContent: String,
    val dislikedByCurrentUser: Boolean,
    val dislikesCount: Int,
    val likedByCurrentUser: Boolean,
    val likesCount: Int,
    val postContent: String,
    val postAuthorUsername: String,
    val postAuthorId: String,
    val postAuthorPicture: String,
    val postId: String,
    val postCreatedAt: String
)

sealed class ProfileScreenEvent {
    object MoreIconClicked : ProfileScreenEvent()
    object DismissMenu : ProfileScreenEvent()
    object LogoutItemClicked : ProfileScreenEvent()
    data class UserIconClicked(val userId: String) : ProfileScreenEvent()
    data class PostClicked(val postId: String) : ProfileScreenEvent()
}
