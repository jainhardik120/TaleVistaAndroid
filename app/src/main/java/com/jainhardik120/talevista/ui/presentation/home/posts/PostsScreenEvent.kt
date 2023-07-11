package com.jainhardik120.talevista.ui.presentation.home.posts

import com.jainhardik120.talevista.ui.components.PostCardEvent

sealed class PostsScreenEvent {
    object ProfileLogoClicked : PostsScreenEvent()
    data class CardEvent(val event: PostCardEvent, val post: HomePost, val index: Int) :
        PostsScreenEvent()

    data class TabChanged(val index: Int) : PostsScreenEvent()
}