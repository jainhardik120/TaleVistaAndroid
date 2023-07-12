package com.jainhardik120.talevista.ui.presentation.home.createpost

sealed class CreatePostsEvent {
    data class CategoryChanged(val index: Int) : CreatePostsEvent()
    data class PostContentChanged(val string: String) : CreatePostsEvent()
    object SendButtonClicked : CreatePostsEvent()
    object CancelButtonClicked : CreatePostsEvent()
    object DialogConfirmButtonClicked : CreatePostsEvent()
    object DialogDismissed : CreatePostsEvent()
}