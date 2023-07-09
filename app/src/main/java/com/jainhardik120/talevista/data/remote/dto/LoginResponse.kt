package com.jainhardik120.talevista.data.remote.dto

data class LoginResponse(
    val token: String,
    val userId: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val picture: String
)