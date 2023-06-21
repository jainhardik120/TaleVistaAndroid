package com.jainhardik120.talevista.data.remote.dto

data class CommentsItem(
    val __v: Int,
    val _id: String,
    val author: Author,
    val createdAt: String,
    val detail: String,
    val dislikesCount: Int,
    val likedByCurrentUser: Boolean,
    val likesCount: Int,
    val post: String,
    val updatedAt: String
)