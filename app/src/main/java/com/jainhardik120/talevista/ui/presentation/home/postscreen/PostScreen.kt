package com.jainhardik120.talevista.ui.presentation.home.postscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.jainhardik120.talevista.data.remote.dto.AuthorX
import com.jainhardik120.talevista.data.remote.dto.SinglePost

@Composable
fun PostScreen(viewModel: PostViewModel) {
    LaunchedEffect(key1 = Unit, block = {
        viewModel.init()
    })
    val state = viewModel.state
    val post = viewModel.state.post
    if (post != null) {
        LazyColumn(content = {
            item {
                UserCard(author = post.post.author)
            }
            item {
                Text(text = post.post.content)
            }
            item {
                Row {
                    TextField(value = state.newCommentContent, onValueChange = {
                        viewModel.onEvent(PostScreenEvent.NewCommentChanged(it))
                    })
                    Button(onClick = {
                        viewModel.onEvent(PostScreenEvent.CommentPostButtonClicked)
                    }) {
                        Text(text = "Send")
                    }
                }
            }
            itemsIndexed(viewModel.state.comments) { index, item ->
                Text(text = item.detail)
            }
        })
    }
}

@Composable
fun PostScreenContent(post: SinglePost) {
    Column {
        UserCard(author = post.post.author)
        Text(text = post.post.content)
    }
}


@Composable
fun UserCard(author: AuthorX) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(16.dp)
    ) {
        Image(
            imageVector = Icons.Outlined.Person,
            contentDescription = null,
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier.padding(start = 16.dp)
        ) {
            Text(
                text = "${author.first_name} ${author.last_name}",
                style = MaterialTheme.typography.headlineLarge
            )
            Text(
                text = author.username,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}