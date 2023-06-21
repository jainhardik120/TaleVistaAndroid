package com.jainhardik120.talevista.ui.presentation.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.jainhardik120.talevista.data.remote.dto.Post
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.jainhardik120.talevista.util.UiEvent
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel(), navigateUp : (UiEvent.Navigate)->Unit) {
    Column {

        Button(onClick = {
            viewModel.getPosts()
        }) {
            Text(text = "Get Categories")
        }
        LazyColumn(content = {
            itemsIndexed(viewModel.state.posts) { _, item ->
                PostCard(post = item)
            }
        })
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PostCard(post: Post) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = post.author.username,
                    modifier = Modifier.weight(1f)
                )
                Chip(content = { Text(text = post.category) })
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = post.content.take(200),
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                ReactionButton(
                    icon = Icons.Default.ThumbUp,
                    count = post.likesCount,
                    highlighted = post.likedByCurrentUser
                )
                ReactionButton(
                    icon = Icons.Default.ThumbDown,
                    count = post.dislikesCount,
                    highlighted = post.dislikedByCurrentUser
                )
                ReactionButton(icon = Icons.Default.Comment, count = 0)
                TimeAgoText(dateTimeString = post.createdAt)
            }
        }
    }
}

@Composable
fun ReactionButton(icon: ImageVector, count: Int, highlighted: Boolean = false) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
//            tint = if (highlighted) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = count.toString())
    }
}

@Composable
fun Chip(content: @Composable () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.padding(start = 8.dp)
    ) {
        Box(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
            content()
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimeAgoText(dateTimeString: String) {
    val currentDateTime = LocalDateTime.now()
    val currentZone = ZoneId.systemDefault()
    val dateTime = LocalDateTime.parse(dateTimeString, DateTimeFormatter.ISO_DATE_TIME)
    val gmtZone = ZoneId.of("GMT")

    val difference = Duration.between(
        dateTime.atZone(gmtZone).toInstant(),
        currentDateTime.atZone(currentZone).toInstant()
    )
    val timeAgoText = getTimeAgoText(difference)

    Text(text = timeAgoText)
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
