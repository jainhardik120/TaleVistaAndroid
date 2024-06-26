package com.jainhardik120.talevista.ui.presentation.home.postscreen

sealed class PostScreenEvent {
    object CommentPostButtonClicked : PostScreenEvent()
    object DeletePostButtonClicked : PostScreenEvent()
    object EditPostButtonClicked : PostScreenEvent()
    data class NewCommentChanged(val string: String) : PostScreenEvent()
    object PostAuthorClicked : PostScreenEvent()
    data class CommentAuthorClicked(val id: String) : PostScreenEvent()
    data class DeleteCommentClicked(val id: String) : PostScreenEvent()
    object LikeButtonClicked : PostScreenEvent()
    object DislikeButtonClicked : PostScreenEvent()
}