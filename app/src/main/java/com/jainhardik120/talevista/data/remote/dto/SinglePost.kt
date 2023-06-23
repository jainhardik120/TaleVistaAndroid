package com.jainhardik120.talevista.data.remote.dto

data class SinglePost(
    val commentCount: Int,
    val dislikedByCurrentUser: Boolean,
    val likedByCurrentUser: Boolean,
    val post: PostX
)