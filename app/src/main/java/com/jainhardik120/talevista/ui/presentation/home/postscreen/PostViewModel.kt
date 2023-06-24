package com.jainhardik120.talevista.ui.presentation.home.postscreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jainhardik120.talevista.data.remote.dto.CommentsItem
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
    var postId = ""

    fun init() {
        postId = savedStateHandle.get<String>("postId") ?: return
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
            when (val response = postsRepository.getPostComments(postId)) {
                is Resource.Error -> {

                }

                is Resource.Success -> {
                    if (response.data != null) {
                        state = state.copy(comments = response.data)
                    }
                }
            }
        }
    }

    fun onEvent(event: PostScreenEvent) {
        when (event) {
            PostScreenEvent.CommentPostButtonClicked -> {
                if (state.newCommentContent.isNotBlank()) {
                    viewModelScope.launch {
                        when (postsRepository.createComment(postId, state.newCommentContent)) {
                            is Resource.Error -> {

                            }

                            is Resource.Success -> {

                            }
                        }
                    }
                }
            }

            is PostScreenEvent.NewCommentChanged -> {
                state = state.copy(newCommentContent = event.string)
            }
        }

    }
}

data class PostState(
    val post: SinglePost? = null,
    val isAuthorUser: Boolean = false,
    val comments: List<CommentsItem> = emptyList(),
    val newCommentContent: String = ""
)

sealed class PostScreenEvent {
    object CommentPostButtonClicked : PostScreenEvent()
    data class NewCommentChanged(val string: String) : PostScreenEvent()
}