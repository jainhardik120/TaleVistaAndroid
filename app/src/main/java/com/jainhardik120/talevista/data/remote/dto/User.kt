package com.jainhardik120.talevista.data.remote.dto

data class User(
    val postsCount: Int?,
    val commentCount: Int?,
    val likeCount: Int?,
    val user: UserInfo
)

data class UserInfo(
    val _id: String?,
    val createdAt: String,
    val date_of_birth: String?,
    val email: String?,
    val first_name: String?,
    val gender: String?,
    val last_name: String?,
    val picture: String?,
    val username: String?,
    val verified: Boolean?
)