package com.jainhardik120.talevista.domain.repository

import com.jainhardik120.talevista.data.remote.dto.User
import com.jainhardik120.talevista.util.Resource

interface UserRepository {

    suspend fun getSelfUserDetails(): Resource<User>

    suspend fun getByUsername(username: String): Resource<User>

    suspend fun getByUserId(userId: String): Resource<User>
}