package com.jainhardik120.talevista.ui.presentation.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jainhardik120.talevista.data.remote.dto.CategoriesItem
import com.jainhardik120.talevista.domain.repository.AuthController
import com.jainhardik120.talevista.domain.repository.PostsRepository
import com.jainhardik120.talevista.util.Resource
import com.jainhardik120.talevista.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val postsRepository: PostsRepository,
    private val authController: AuthController
) : ViewModel() {
    var state by mutableStateOf(HomeState())


    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            Log.d("TAG", "sendUiEvent: ")
            _uiEvent.send(event)
        }
    }

    init {
        state = state.copy(selfId = authController.getUserId() ?: "")
        viewModelScope.launch {
            when (val result = postsRepository.getCategories()) {
                is Resource.Error -> {
                    sendUiEvent(UiEvent.ShowSnackbar(result.message ?: "Unknown Error"))
                }

                is Resource.Success -> {
                    state = state.copy(categories = result.data ?: emptyList())
                }
            }
        }
    }
}

data class HomeState(
    val selfId: String = "",
    val categories: List<CategoriesItem> = emptyList()
)