package com.jainhardik120.talevista.ui.presentation.home.createpost

import com.jainhardik120.talevista.data.remote.dto.CategoriesItem

data class CreatePostState(
    val selectedCategory: Int = 0,
    val postContent: String = "",
    val categories: List<CategoriesItem> = emptyList()
)