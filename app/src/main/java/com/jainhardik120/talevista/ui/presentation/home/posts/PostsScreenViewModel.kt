package com.jainhardik120.talevista.ui.presentation.home.posts

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jainhardik120.talevista.data.remote.PostsApi
import com.jainhardik120.talevista.data.remote.dto.CategoriesItem
import com.jainhardik120.talevista.data.remote.dto.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostsScreenViewModel @Inject constructor(private val postsApi: PostsApi) : ViewModel() {
    var state by mutableStateOf(PostsScreenState())

    init {
        viewModelScope.launch {
            try {
                val categories = postsApi.getCategories()
                state = state.copy(categories = categories)
                val posts = postsApi.getPosts()
                state = state.copy(posts = posts.posts)
            } catch (e: Exception) {
                Log.d("TAG", "Init: ${e.message}")
            }
        }
    }
}

data class PostsScreenState(
    val categories: List<CategoriesItem> = emptyList(),
    val posts: List<Post> = emptyList()
)