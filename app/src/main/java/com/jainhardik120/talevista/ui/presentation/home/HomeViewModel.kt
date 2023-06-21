package com.jainhardik120.talevista.ui.presentation.home

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
import java.util.Locale.Category
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val postsApi: PostsApi
) : ViewModel() {
    var state by mutableStateOf(HomeState())

    fun getCategories(){
        viewModelScope.launch {
            try {
                val categories = postsApi.getCategories()
                state = state.copy(categories = categories)

            }catch (e : Exception){
                Log.d("TAG", "Init: ${e.message}")
            }
        }

    }
    fun getPosts(){
        viewModelScope.launch {
            try {
                val categories = postsApi.getPosts()
                Log.d("TAG", "getPosts: $categories")
                state = state.copy(posts = categories.posts)
            }catch (e : Exception){
                Log.d("TAG", "Init: ${e.message}")
            }
        }
    }
}

data class HomeState(
    val categories: List<CategoriesItem> = emptyList(),
    val posts: List<Post> = emptyList()
)