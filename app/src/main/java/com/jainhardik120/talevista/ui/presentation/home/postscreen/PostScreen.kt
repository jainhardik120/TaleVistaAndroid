package com.jainhardik120.talevista.ui.presentation.home.postscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Report
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material.icons.rounded.Comment
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.jainhardik120.talevista.data.remote.dto.CommentsItem
import com.jainhardik120.talevista.data.remote.dto.SinglePost
import com.jainhardik120.talevista.util.UiEvent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostScreen(viewModel: PostViewModel, navController: NavController) {
    LaunchedEffect(key1 = Unit, block = {
        viewModel.init()
        viewModel.uiEvent.collect {
            when (it) {
                is UiEvent.Navigate -> {
                    if (it.route == "NAVIGATE_BACK") {
                        navController.navigateUp()
                    } else {
                        navController.navigate(it.route)
                    }
                }

                is UiEvent.ShowSnackbar -> {

                }
            }
        }
    })
    val state = viewModel.state
    val post = viewModel.state.post
    val topAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val coroutineScope = rememberCoroutineScope()
    if (post != null) {
        val author = post.post.author
        val lazyColumnListState = rememberLazyListState()
        val areCommentsVisible by remember {
            derivedStateOf {
                (lazyColumnListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) != 0
            }
        }
        Column(Modifier.imePadding()) {
            Scaffold(modifier = Modifier
                .fillMaxSize()
                .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
                topBar = {
                    TopAppBar(navigationIcon = {
                        IconButton(onClick = {
                            viewModel.onEvent(PostScreenEvent.PostAuthorClicked)
                        }) {
                            Box(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .clip(RoundedCornerShape(100))
                            ) {
                                AsyncImage(
                                    model = post.post.author.picture,
                                    contentDescription = "ProfileIcon"
                                )
                            }
                        }
                    },
                        scrollBehavior = topAppBarScrollBehavior,
                        title = {
                            Text(
                                text = author.username
                            )
                        },
                        actions = {
                            var expanded by remember { mutableStateOf(false) }
                            Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
                                IconButton(onClick = { expanded = true }) {
                                    Icon(
                                        Icons.Default.MoreVert,
                                        contentDescription = "Localized description"
                                    )
                                }
                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    if (state.isAuthorUser) {

                                        DropdownMenuItem(
                                            text = { Text("Edit") },
                                            onClick = {
                                                expanded = false
                                            },
                                            leadingIcon = {
                                                Icon(
                                                    Icons.Outlined.Edit,
                                                    contentDescription = null
                                                )
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Delete") },
                                            onClick = {
                                                expanded = false
                                                viewModel.onEvent(PostScreenEvent.DeletePostButtonClicked)
                                            },
                                            leadingIcon = {
                                                Icon(
                                                    Icons.Outlined.Delete,
                                                    contentDescription = null
                                                )
                                            }
                                        )
                                    } else {
                                        DropdownMenuItem(
                                            text = { Text("Report") },
                                            onClick = {
                                                expanded = false
                                            },
                                            leadingIcon = {
                                                Icon(
                                                    Icons.Outlined.Report,
                                                    contentDescription = null
                                                )
                                            }
                                        )

                                    }
                                }
                            }
                        })
                },
                floatingActionButton = {
                    if (!areCommentsVisible) {
                        FloatingActionButton(onClick = {
                            coroutineScope.launch {
                                lazyColumnListState.animateScrollToItem(2, -1)
                            }
                        }) {
                            Icon(Icons.Rounded.Comment, contentDescription = "Comment Icon")
                        }
                    }
                }) { padding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    LazyColumn(state = lazyColumnListState) {
                        item(key = "POST_CONTENT") {
                            Text(
                                text = post.post.content, modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp)
                            )
                        }
                        item(key = "POST_FOOTER_CONTENT") {
                            Row {
                                Text(text = "Like")
                            }
                        }
                        item {
                            Box(
                                modifier = Modifier
                                    .height(56.dp)
                                    .fillMaxWidth()
                            ) {
                                Row {
                                    BasicTextField(
                                        value = state.newCommentContent,
                                        onValueChange = {
                                            viewModel.onEvent(
                                                PostScreenEvent.NewCommentChanged(
                                                    it
                                                )
                                            )
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxSize()
                                            .padding(horizontal = 32.dp),
                                        cursorBrush = SolidColor(LocalContentColor.current),
                                        textStyle = LocalTextStyle.current.copy(color = LocalContentColor.current)
                                    )
                                    Button(onClick = {
                                        viewModel.onEvent(PostScreenEvent.CommentPostButtonClicked)
                                    }) {
                                        Text(text = "Send")
                                    }
                                }
                            }
                        }
                        comments(post, viewModel.state.comments)
                    }
                }

            }
        }
    }
}

fun LazyListScope.comments(post: SinglePost, comments: List<CommentsItem>) {

    itemsIndexed(comments) { index, item ->
        Card(
            Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Row {
                Image(Icons.Filled.Person, contentDescription = "Person Icon")
                Column {
                    Text(text = item.author.username)
                    Text(text = item.detail)
                }
            }
            Row {
                FilledTonalIconButton(
                    onClick = {

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
                    text = item.likesCount.toString(),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(10.dp)
                )
                FilledTonalIconButton(
                    onClick = {

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