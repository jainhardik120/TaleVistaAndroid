package com.jainhardik120.talevista.ui.presentation.home.posts

import com.jainhardik120.talevista.data.remote.dto.Post
import com.jainhardik120.talevista.ui.components.PostCardEvent

sealed class PostsScreenEvent {
    object ProfileLogoClicked : PostsScreenEvent()
    data class CardEvent(val event: PostCardEvent, val post: Post, val index: Int) :
        PostsScreenEvent()
}