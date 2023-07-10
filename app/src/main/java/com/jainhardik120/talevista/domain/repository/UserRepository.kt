package com.jainhardik120.talevista.domain.repository

import com.jainhardik120.talevista.data.remote.dto.Posts
import com.jainhardik120.talevista.data.remote.dto.SearchResult
import com.jainhardik120.talevista.data.remote.dto.User
import com.jainhardik120.talevista.util.Resource
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun getSelfUserDetails(): Resource<User>

    suspend fun getByUsername(username: String): Resource<User>

    suspend fun getByUserId(userId: String): Resource<User>

    suspend fun getPostsLikedByUser(userId: String, page: Int): Flow<Posts>

    suspend fun searchUsers(query: String): Resource<SearchResult>
}