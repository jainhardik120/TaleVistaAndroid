package com.jainhardik120.talevista.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.jainhardik120.talevista.data.remote.dto.Post

class PostsPagingSource(private val postsApi: PostsApi, private val query: PostsQuery) :
    PagingSource<Int, Post>() {
    override fun getRefreshKey(state: PagingState<Int, Post>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Post> {
        val pageIndex = params.key ?: 1
        return try {
            val response = postsApi.getPosts(
                page = pageIndex,
                limit = params.loadSize,
                userId = query.userId,
                category = query.category
            )
            val nextKey = if (response.posts.isEmpty()) {
                null
            } else {
                pageIndex + (params.loadSize / 4)
            }
            LoadResult.Page(
                data = response.posts,
                prevKey = if (pageIndex == 1) null else pageIndex,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}

data class PostsQuery(
    val userId: String? = null,
    val category: String? = null
)