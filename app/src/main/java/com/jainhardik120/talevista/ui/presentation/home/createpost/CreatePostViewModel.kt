package com.jainhardik120.talevista.ui.presentation.home.createpost

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jainhardik120.talevista.data.remote.PostsApi
import com.jainhardik120.talevista.data.remote.dto.CategoriesItem
import com.jainhardik120.talevista.domain.repository.PostsRepository
import com.jainhardik120.talevista.util.Resource
import com.jainhardik120.talevista.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class CreatePostViewModel @Inject constructor(
    private val postsRepository: PostsRepository
) : ViewModel() {
    var state by mutableStateOf(CreatePostState())

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            Log.d("TAG", "sendUiEvent: ")
            _uiEvent.send(event)
        }
    }

    fun setCategories(categories: List<CategoriesItem>) {
        state = state.copy(categories = categories)
    }


    private fun RequestBody(vararg pairs: Pair<String, String>): RequestBody {
        return JSONObject(pairs.toMap()).toString()
            .toRequestBody("application/json".toMediaTypeOrNull())
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
                    viewModelScope.launch {
                        when (val result = postsRepository.createPost(
                            state.postContent,
                            state.categories[state.selectedCategory].shortName
                        )) {
                            is Resource.Error -> {
                                sendUiEvent(UiEvent.ShowSnackbar(result.message ?: "Unknown Error"))
                            }

                            is Resource.Success -> {
                                val message = result.data?.message
                                if (message != null) {
                                    sendUiEvent(UiEvent.ShowSnackbar(message))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


data class CreatePostState(
    val selectedCategory: Int = 0,
    val postContent: String = "",
    val categories: List<CategoriesItem> = emptyList()
)

sealed class CreatePostsEvent {
    data class CategoryChanged(val index: Int) : CreatePostsEvent()
    data class PostContentChanged(val string: String) : CreatePostsEvent()
    object SendButtonClicked : CreatePostsEvent()
}