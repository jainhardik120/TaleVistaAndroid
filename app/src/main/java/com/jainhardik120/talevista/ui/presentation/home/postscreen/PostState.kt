package com.jainhardik120.talevista.ui.presentation.home.postscreen

import com.jainhardik120.talevista.data.remote.dto.CommentsItem
import com.jainhardik120.talevista.data.remote.dto.SinglePost

data class PostState(
    val post: SinglePost? = null,
    val isAuthorUser: Boolean = false,
    val comments: List<CommentsItem> = emptyList(),
    val newCommentContent: String = "",
    val selfUserPicture: String = ""
)