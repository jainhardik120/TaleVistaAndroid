package com.jainhardik120.talevista.data.remote.dto

data class Post(
    val __v: Int,
    val _id: String,
    val author: Author,
    val category: String,
    val content: String,
    val createdAt: String,
    val dislikedByCurrentUser: Boolean,
    val dislikesCount: Int,
    val likedByCurrentUser: Boolean,
    val likesCount: Int,
    val updatedAt: String
)