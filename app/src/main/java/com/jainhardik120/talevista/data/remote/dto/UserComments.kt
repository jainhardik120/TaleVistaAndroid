package com.jainhardik120.talevista.data.remote.dto

data class UserComments(
    val comments: List<Comment>,
    val currentPage: Int,
    val totalPages: Int,
    val totalPosts: Int
)