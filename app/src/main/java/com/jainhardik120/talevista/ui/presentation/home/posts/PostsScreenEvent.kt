package com.jainhardik120.talevista.ui.presentation.home.posts

sealed class PostsScreenEvent {
    data class LikeButtonClicked(val index: Int) : PostsScreenEvent()
    data class DislikeButtonClicked(val index: Int) : PostsScreenEvent()
    data class PostClicked(val postId: String) : PostsScreenEvent()
    object ProfileLogoClicked : PostsScreenEvent()
    data class PostAuthorClicked(val authorId: String) : PostsScreenEvent()
}