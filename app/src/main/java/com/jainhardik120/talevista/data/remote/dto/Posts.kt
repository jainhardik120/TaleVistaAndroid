package com.jainhardik120.talevista.data.remote.dto

data class Posts(
    val currentPage: Int,
    val posts: List<Post>,
    val totalPages: Int,
    val totalPosts: Int
)

data class Comments(
    val currentPage: Int,
    val comments: List<CommentsItem>,
    val totalPages: Int,
    val totalPosts: Int
)

