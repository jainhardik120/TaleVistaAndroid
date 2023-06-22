package com.jainhardik120.talevista.ui.presentation.home.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jainhardik120.talevista.data.remote.UsersApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileScreenViewModel @Inject constructor(
    private val usersApi: UsersApi
) : ViewModel() {

    var state by mutableStateOf(ProfileState())

    init {
        viewModelScope.launch {
            try {
                val response = usersApi.getSelfDetails()
                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) {
                        state = state.copy(
                            createdAt = user.createdAt,
                            email = user.email,
                            firstName = user.first_name,
                            lastName = user.last_name,
                            username = user.username,
                            verified = user.verified
                        )
                    }
                }
            } catch (_: Exception) {

            }
        }
    }
}

data class ProfileState(
    val createdAt: String = "",
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val username: String = "",
    val verified: Boolean = false
)