package com.jainhardik120.talevista.ui.presentation.home.posts

import com.jainhardik120.talevista.data.remote.dto.CategoriesItem

data class PostsScreenState(
    val profileImageUrl: String = "",
    val categories: List<CategoriesItem> = emptyList(),
    val selectedCategoryIndex: Int = 0
)