package com.jainhardik120.talevista.ui.presentation.home.postscreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Report
import androidx.compose.material.icons.rounded.Comment
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.jainhardik120.talevista.ui.presentation.home.posts.ListState
import com.jainhardik120.talevista.util.NAVIGATE_UP_ROUTE
import com.jainhardik120.talevista.util.UiEvent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun PostScreen(viewModel: PostViewModel, navController: NavController) {
    LaunchedEffect(key1 = Unit, block = {
        viewModel.init()
        viewModel.uiEvent.collect {
            when (it) {
                is UiEvent.Navigate -> {
                    if (it.route == NAVIGATE_UP_ROUTE) {
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
    val lazyColumnListState = rememberLazyListState()
    val shouldStartPaginate = remember {
        derivedStateOf {
            viewModel.canPaginate && (lazyColumnListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
                ?: -9) >= (lazyColumnListState.layoutInfo.totalItemsCount - 6)
        }
    }
    LaunchedEffect(key1 = shouldStartPaginate.value, block = {
        if (shouldStartPaginate.value && viewModel.listState == ListState.IDLE) {
            viewModel.getComments()
        }
    })
    val comments by viewModel.comments.collectAsState()
    if (post != null) {
        val author = post.post.author
        val areCommentsVisible by remember {
            derivedStateOf {
                (lazyColumnListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) != 0
            }
        }
        Column(Modifier.imePadding()) {
            Scaffold(modifier = Modifier
                .fillMaxSize()
                .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection), topBar = {
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
                }, scrollBehavior = topAppBarScrollBehavior, title = {
                    Text(
                        text = author.username
                    )
                }, actions = {
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
                            onDismissRequest = { expanded = false }) {
                            if (state.isAuthorUser) {

                                DropdownMenuItem(text = { Text("Edit") }, onClick = {
                                    expanded = false
                                    viewModel.onEvent(PostScreenEvent.EditPostButtonClicked)
                                }, leadingIcon = {
                                    Icon(
                                        Icons.Outlined.Edit, contentDescription = null
                                    )
                                })
                                DropdownMenuItem(text = { Text("Delete") }, onClick = {
                                    expanded = false
                                    viewModel.onEvent(PostScreenEvent.DeletePostButtonClicked)
                                }, leadingIcon = {
                                    Icon(
                                        Icons.Outlined.Delete, contentDescription = null
                                    )
                                })
                            } else {
                                DropdownMenuItem(text = { Text("Report") }, onClick = {
                                    expanded = false
                                }, leadingIcon = {
                                    Icon(
                                        Icons.Outlined.Report, contentDescription = null
                                    )
                                })

                            }
                        }
                    }
                })
            }, floatingActionButton = {
                AnimatedVisibility(
                    visible = !areCommentsVisible,
                    enter = scaleIn(),
                    exit = scaleOut()
                ) {
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
                                text = post.post.content,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp)
                            )
                        }
                        item(key = "POST_FOOTER_CONTENT") {
                            Row {}
                        }
                        item {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)) {
                                OutlinedTextField(
                                    value = state.newCommentContent,
                                    modifier = Modifier.fillMaxWidth(),
                                    onValueChange = {
                                        viewModel.onEvent(
                                            PostScreenEvent.NewCommentChanged(
                                                it
                                            )
                                        )
                                    },
                                    trailingIcon = {
                                        IconButton(
                                            onClick = { viewModel.onEvent(PostScreenEvent.CommentPostButtonClicked) },
                                            enabled = state.newCommentContent.isNotBlank()
                                        ) {
                                            Icon(
                                                Icons.Rounded.Send,
                                                contentDescription = "Send Icon"
                                            )
                                        }
                                    },
                                    leadingIcon = {
                                        AsyncImage(
                                            model = state.selfUserPicture,
                                            contentDescription = "Profile Icon",
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(
                                                    RoundedCornerShape(100)
                                                )
                                        )
                                    }
                                )
                            }
                        }
                        itemsIndexed(comments, key = { _, item -> item._id }) { _, item ->
                            Column(

                                Modifier.padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(40.dp)
                                ) {
                                    Box(modifier = Modifier
                                        .size(40.dp)
                                        .padding(4.dp)
                                        .clip(RoundedCornerShape(100))
                                        .clickable {

                                        }) {
                                        AsyncImage(
                                            model = item.author.picture,
                                            contentDescription = "ProfileIcon"
                                        )
                                    }
                                    Column(
                                        modifier = Modifier.padding(
                                            horizontal = 12.dp,
                                            vertical = 4.dp
                                        ), verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = "@${item.author.username}",
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                        Text(
                                            text = item.createdAt,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.outline,
                                            maxLines = 1
                                        )
                                    }
                                    Spacer(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxSize()
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = item.detail,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.fillMaxWidth(),
                                    maxLines = 4
                                )

                            }
                            Divider()
                        }
                    }
                }

            }
        }
    }
}
