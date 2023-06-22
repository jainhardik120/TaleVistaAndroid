package com.jainhardik120.talevista.ui.presentation.home.createpost

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jainhardik120.talevista.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreatePostViewModel @Inject constructor() : ViewModel() {
    var state by mutableStateOf(CreatePostState())

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            Log.d("TAG", "sendUiEvent: ")
            _uiEvent.send(event)
        }
    }

    fun onEvent(event: CreatePostsEvent) {
        when (event) {
            is CreatePostsEvent.CategoryChanged -> {
                state = state.copy(selectedCategory = event.index)
            }

            is CreatePostsEvent.PostContentChanged -> {
                state = state.copy(postContent = event.string)
            }
        }

    }
}


data class CreatePostState(
    val selectedCategory: Int = 0,
    val postContent: String = ""
)

sealed class CreatePostsEvent {
    data class CategoryChanged(val index: Int) : CreatePostsEvent()
    data class PostContentChanged(val string: String) : CreatePostsEvent()
}