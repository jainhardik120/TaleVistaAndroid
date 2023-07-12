package com.jainhardik120.talevista.ui.presentation.home.posts

import android.os.Build
import android.text.format.DateUtils
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jainhardik120.talevista.data.remote.PostsQuery
import com.jainhardik120.talevista.data.remote.dto.Author
import com.jainhardik120.talevista.data.remote.dto.CategoriesItem
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
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.TimeZone
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

    private var categoryMap: MutableMap<String, Pair<String, Int>> = mutableMapOf()

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    private val _posts = MutableStateFlow<List<HomePost>>(emptyList())
    val posts: StateFlow<List<HomePost>> get() = _posts.asStateFlow()

    private var page by mutableStateOf(1)
    var canPaginate by mutableStateOf(false)
    var listState by mutableStateOf(ListState.IDLE)

    init {
        state =
            state.copy(profileImageUrl = authController.getUserInfo(UserPreferences.PICTURE) ?: "")
        handleRepositoryResponse({ postsRepository.getCategories() }) {
            it.forEachIndexed { index, item ->
                categoryMap[item.shortName] = Pair(item.name, index + 1)
            }
            val list = mutableListOf(
                CategoriesItem("All", "all")
            )
            list.addAll(it)
            state = state.copy(categories = list)
        }
    }

    private fun clearAndLoad() {
        page = 1
        canPaginate = false
        listState = ListState.IDLE
        _posts.value = emptyList()
        loadList()
    }

    fun loadList() {
        if (state.selectedCategoryIndex == 0) {
            getPosts()
        } else {
            getPosts(state.categories[state.selectedCategoryIndex].shortName)
        }
    }

    private fun getPosts(category: String? = null) = viewModelScope.launch {
        if (page == 1 || (page != 1 && canPaginate) && listState == ListState.IDLE) {
            listState = if (page == 1) ListState.LOADING else ListState.PAGINATING
            postsRepository.getPostsCustom(page, PostsQuery(category = category)).collect {
                if (it.currentPage != 0) {
                    canPaginate = it.currentPage < it.totalPages
                    if (page == 1) {
                        _posts.value = it.posts.map {
                            it.toHomePost()
                        }
                    } else {
                        _posts.value = _posts.value + it.posts.map { it.toHomePost() }
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

    private fun onPostCardEvent(event: PostCardEvent, post: HomePost, index: Int) {
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

            is PostsScreenEvent.TabChanged -> {
                Log.d("TAG", "onEvent: ${event.index}")
                state = state.copy(selectedCategoryIndex = event.index)
                clearAndLoad()
            }
        }
    }

    private fun Post.toHomePost(): HomePost {
        return HomePost(
            this._id,
            this.author,
            categoryMap[this.category]?.first ?: this.category,
            categoryMap[this.category]?.second ?: 0,
            this.content,
            timeAgoText(this.createdAt),
            this.likedByCurrentUser,
            this.dislikedByCurrentUser,
            this.likesCount
        )
    }

    private fun timeAgoText(text: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("GMT")
        try {
            val date: Date? = sdf.parse(text)
            if (date != null) {
                val timeInMillis: Long = date.time
                val currentTimeInMillis = System.currentTimeMillis()
                return DateUtils.getRelativeTimeSpanString(
                    timeInMillis,
                    currentTimeInMillis,
                    DateUtils.MINUTE_IN_MILLIS
                ).toString()
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val currentDateTime = LocalDateTime.now()
            val currentZone = ZoneId.systemDefault()
            val dateTime = LocalDateTime.parse(text, DateTimeFormatter.ISO_DATE_TIME)
            val gmtZone = ZoneId.of("GMT")

            val difference = Duration.between(
                dateTime.atZone(gmtZone).toInstant(),
                currentDateTime.atZone(currentZone).toInstant()
            )
            val seconds = difference.seconds
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24
            val weeks = days / 7

            when {
                weeks > 0 -> "$weeks week${if (weeks > 1) "s" else ""} ago"
                days > 0 -> "$days day${if (days > 1) "s" else ""} ago"
                hours > 0 -> "$hours hour${if (hours > 1) "s" else ""} ago"
                minutes > 0 -> "$minutes minute${if (minutes > 1) "s" else ""} ago"
                else -> "$seconds second${if (seconds > 1) "s" else ""} ago"
            }
        } else {
            text
        }
    }
}

data class HomePost(
    val _id: String,
    val author: Author,
    val category: String,
    val categoryIndex: Int,
    val content: String,
    val createdAt: String,
    val likedByCurrentUser: Boolean,
    val dislikedByCurrentUser: Boolean,
    val likesCount: Int,
)

