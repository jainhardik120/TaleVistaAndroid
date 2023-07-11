package com.jainhardik120.talevista.ui.components

sealed class PostCardEvent {
    object AuthorClicked : PostCardEvent()
    object PostClicked : PostCardEvent()
    object LikeButtonClicked : PostCardEvent()
    object DislikeButtonClicked : PostCardEvent()
}