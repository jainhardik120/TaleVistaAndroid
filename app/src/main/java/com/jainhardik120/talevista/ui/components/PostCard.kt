package com.jainhardik120.talevista.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.jainhardik120.talevista.data.remote.dto.Post

@Composable
fun PostCard(
    post: Post,
    onEvent: (PostCardEvent) -> Unit,
    index: Int,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
        ),
        modifier = Modifier
            .padding(top = 12.dp, start = 12.dp, end = 12.dp)
            .clickable(
                onClick = { onEvent(PostCardEvent.PostClicked) }
            ),
        shape = RoundedCornerShape(10.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier
                    .size(40.dp)
                    .padding(4.dp)
                    .clip(RoundedCornerShape(100))
                    .clickable {
                        onEvent(PostCardEvent.AuthorClicked)
                    }) {
                    AsyncImage(
                        model = post.author.picture,
                        contentDescription = "ProfileIcon"
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = post.author.username,
                        style = MaterialTheme.typography.labelMedium
                    )
                    TimeAgoText(dateTimeString = post.createdAt)
                }
                Spacer(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
                SuggestionChip(onClick = {}, label = {
                    Text(text = post.category)
                })
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = post.content,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 4
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                FilledTonalIconButton(
                    onClick = {
                        onEvent(PostCardEvent.LikeButtonClicked)
                    },
                    modifier = Modifier.size(35.dp)
                ) {
                    Icon(
                        if (post.likedByCurrentUser) {
                            Icons.Filled.ThumbUp
                        } else {
                            Icons.Outlined.ThumbUp
                        },
                        contentDescription = "Like",
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = post.likesCount.toString(),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(10.dp)
                )
                FilledTonalIconButton(
                    onClick = {
                        onEvent(PostCardEvent.DislikeButtonClicked)
                    },
                    modifier = Modifier.size(35.dp)
                ) {
                    Icon(
                        if (post.dislikedByCurrentUser) {
                            Icons.Filled.ThumbDown
                        } else {
                            Icons.Outlined.ThumbDown
                        },
                        contentDescription = "Dislike",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

