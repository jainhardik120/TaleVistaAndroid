package com.jainhardik120.talevista.data.remote.dto

data class PostX(
    val __v: Int,
    val _id: String,
    val author: AuthorX,
    val category: String,
    val content: String,
    val createdAt: String,
    val dislikesCount: Int,
    val likesCount: Int,
    val updatedAt: String
)