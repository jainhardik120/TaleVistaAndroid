package com.jainhardik120.talevista.ui.presentation.home.profile

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jainhardik120.talevista.domain.repository.AuthController
import com.jainhardik120.talevista.domain.repository.UserRepository
import com.jainhardik120.talevista.util.Resource
import com.jainhardik120.talevista.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileScreenViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val authController: AuthController,
    private val usersRepository: UserRepository
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
        val userId = savedStateHandle.get<String>("userId") ?: return
        val selfId = authController.getUserId() ?: return
        state = state.copy(userId = userId, isSelfUser = (userId == selfId))
        loadUser()
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
}
