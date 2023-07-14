package com.jainhardik120.talevista.data.remote.dto

data class Comment(
    val __v: Int,
    val _id: String,
    val createdAt: String,
    val detail: String,
    val dislikedByCurrentUser: Boolean,
    val dislikesCount: Int,
    val likedByCurrentUser: Boolean,
    val likesCount: Int,
    val post: PostXX,
    val updatedAt: String
)