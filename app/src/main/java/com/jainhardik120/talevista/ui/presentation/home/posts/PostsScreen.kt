package com.jainhardik120.talevista.ui.presentation.home.posts

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.jainhardik120.talevista.data.remote.dto.Post
import com.jainhardik120.talevista.util.UiEvent
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun PostsScreen(viewModel: PostsScreenViewModel, onNavigate: (String) -> Unit) {
    LaunchedEffect(key1 = Unit, block = {
        viewModel.getPosts()
        viewModel.uiEvent.collect {
            when (it) {
                is UiEvent.Navigate -> {
                    onNavigate(it.route)
                }

                is UiEvent.ShowSnackbar -> {

                }
            }
        }
    })
//    val posts = viewModel.postsPagingFlow.collectAsLazyPagingItems()
//    PostsContainer(posts = posts, onEvent = viewModel::onEvent)


    val lazyColumnListState = rememberLazyListState()

    val shouldStartPaginate = remember {
        derivedStateOf {
            viewModel.canPaginate && (lazyColumnListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
                ?: -9) >= (lazyColumnListState.layoutInfo.totalItemsCount - 6)
        }
    }

    val posts by viewModel.posts.collectAsState()
    LaunchedEffect(key1 = shouldStartPaginate.value, block = {
        if (shouldStartPaginate.value && viewModel.listState == ListState.IDLE)
            viewModel.getPosts()
    })
    LazyColumn(state = lazyColumnListState) {
        itemsIndexed(posts) { index, item ->
            PostCard(post = item, onEvent = viewModel::onEvent, index = index)
        }
        item {
            when (viewModel.listState) {
                ListState.LOADING -> {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator()
                    }
                }

                ListState.PAGINATING -> {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        CircularProgressIndicator()
                    }
                }

                ListState.PAGINATION_EXHAUST -> {
                    Text(text = "Paginating Exhaust")
                }

                else -> {

                }
            }
        }
    }
}

@Composable
fun PostsContainer(posts: LazyPagingItems<Post>, onEvent: (PostsScreenEvent) -> Unit) {
    val context = LocalContext.current
    LaunchedEffect(key1 = posts.loadState, block = {
        if (posts.loadState.refresh is LoadState.Error) {
            Toast.makeText(
                context,
                "Error: " + (posts.loadState.refresh as LoadState.Error).error.message,
                Toast.LENGTH_LONG
            ).show()
        }
    })
    Box(Modifier.fillMaxSize(1f)) {
        if (posts.loadState.refresh is LoadState.Loading) {
            CircularProgressIndicator(
                Modifier.align(Alignment.Center)
            )
        } else {
            LazyColumn(content = {
                items(count = posts.itemCount) { index ->
                    val post = posts[index]
                    if (post != null) {
                        PostCard(post = post, onEvent = onEvent, index = index)
                    }
                }
                item {
                    if (posts.loadState.append is LoadState.Loading) {
                        CircularProgressIndicator()
                    }
                }
            })
        }
    }
}


@Composable
fun PostCard(post: Post, onEvent: (PostsScreenEvent) -> Unit, index: Int) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
        ),

        modifier = Modifier
            .padding(top = 12.dp, start = 12.dp, end = 12.dp)
            .clickable(
                onClick = { onEvent(PostsScreenEvent.PostClicked(post._id)) }
            ),
        shape = RoundedCornerShape(10.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Image(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    imageVector = Icons.Filled.Person,
                    contentDescription = post.author.username,
                )

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
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = post.content.take(200),
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                FilledTonalIconButton(
                    onClick = {
                        onEvent(PostsScreenEvent.LikeButtonClicked(index))
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
                        onEvent(PostsScreenEvent.DislikeButtonClicked(index))
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

@Composable
fun TimeAgoText(dateTimeString: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val currentDateTime = LocalDateTime.now()
        val currentZone = ZoneId.systemDefault()
        val dateTime = LocalDateTime.parse(dateTimeString, DateTimeFormatter.ISO_DATE_TIME)
        val gmtZone = ZoneId.of("GMT")

        val difference = Duration.between(
            dateTime.atZone(gmtZone).toInstant(),
            currentDateTime.atZone(currentZone).toInstant()
        )
        val timeAgoText = getTimeAgoText(difference)
        Text(
            text = timeAgoText,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.outline
        )
    } else {
        Text(
            text = dateTimeString,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.outline
        )
    }
}


@RequiresApi(Build.VERSION_CODES.O)
private fun getTimeAgoText(duration: Duration): String {
    val seconds = duration.seconds
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24
    val weeks = days / 7

    return when {
        weeks > 0 -> "$weeks week${if (weeks > 1) "s" else ""} ago"
        days > 0 -> "$days day${if (days > 1) "s" else ""} ago"
        hours > 0 -> "$hours hour${if (hours > 1) "s" else ""} ago"
        minutes > 0 -> "$minutes minute${if (minutes > 1) "s" else ""} ago"
        else -> "$seconds second${if (seconds > 1) "s" else ""} ago"
    }
}