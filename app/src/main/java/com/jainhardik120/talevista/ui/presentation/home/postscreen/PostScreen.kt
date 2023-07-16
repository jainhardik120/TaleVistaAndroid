package com.jainhardik120.talevista.ui.presentation.home.postscreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.HeartBroken
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Comment
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.HeartBroken
import androidx.compose.material.icons.rounded.Comment
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.jainhardik120.talevista.R
import com.jainhardik120.talevista.ui.presentation.home.posts.ListState
import com.jainhardik120.talevista.util.NAVIGATE_UP_ROUTE
import com.jainhardik120.talevista.util.UiEvent
import kotlinx.coroutines.launch

//@Composable
//fun PostScreen(viewModel: PostViewModel, navController: NavController) {
//    Column(Modifier.imePadding()) {
//        Scaffold(
//            modifier = Modifier
//                .fillMaxSize()
//        ) {
//            Column(Modifier.padding(it)) {
//                Text(text = "Hardik Jain \nHardik Jain \nHardik Jain \nHardik Jain \nHardik Jain \nHardik Jain \nHardik Jain \nHardik Jain \nHardik Jain \nHardik Jain \nHardik Jain \nHardik Jain \nHardik Jain \nHardik Jain \nHardik Jain \nHardik Jain \nHardik Jain \nHardik Jain \nHardik Jain \nHardik Jain \nHardik Jain \nHardik Jain \nHardik Jain \nHardik Jain \nHardik Jain \nHardik Jain \nHardik Jain \nHardik Jain \nHardik Jain \nHardik Jain \nHardik Jain \nHardik Jain \nHardik Jain \nHardik Jain \nHardik Jain \nHardik Jain \nHardik Jain \nHardik Jain \nHardik Jain \nHardik Jain \nHardik Jain \nHardik Jain \nHardik Jain \n")
//            }
//        }
//    }
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostScreen(viewModel: PostViewModel, navController: NavController) {
    val hostState = remember { SnackbarHostState() }
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
                    hostState.showSnackbar(it.message)
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
    val areCommentsVisible by remember {
        derivedStateOf {
            (lazyColumnListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) != 0
        }
    }
    val isPostVisible by remember {
        derivedStateOf {
            (lazyColumnListState.layoutInfo.visibleItemsInfo.firstOrNull()?.index ?: 0) == 0
        }
    }
    Column(Modifier.imePadding()) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
            snackbarHost = { SnackbarHost(hostState = hostState) },
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
                                model = post?.post?.author?.picture,
                                contentDescription = "ProfileIcon"
                            )
                        }
                    }
                }, scrollBehavior = topAppBarScrollBehavior, title = {
                    Text(
                        text = post?.post?.author?.username
                            ?: stringResource(id = R.string.app_name)
                    )
                }, actions = {
                    var expanded by remember { mutableStateOf(false) }
                    if (state.isAuthorUser) {
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
                            }
                        }
                    }
                })
            },
            floatingActionButton = {
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
                AnimatedVisibility(
                    visible = !isPostVisible,
                    enter = scaleIn(),
                    exit = scaleOut()
                ) {
                    FloatingActionButton(onClick = {
                        coroutineScope.launch {
                            lazyColumnListState.animateScrollToItem(0, -1)
                        }
                    }) {
                        Icon(Icons.Rounded.KeyboardArrowUp, contentDescription = "Up Icon")
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
                            text = post?.post?.content ?: "",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp)
                        )
                    }
                    item(key = "POST_FOOTER_CONTENT") {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                val liked = state.liked
                                val disliked = state.disliked
                                IconButtonWithText(
                                    checked = liked,
                                    onCheckedChange = {
                                        viewModel.onEvent(PostScreenEvent.LikeButtonClicked)
                                    },
                                    count = state.likeCount,
                                    checkedIcon = Icons.Filled.Favorite,
                                    unCheckedIcon = Icons.Filled.FavoriteBorder
                                )
                                IconButtonWithText(
                                    checked = disliked,
                                    onCheckedChange = {
                                        viewModel.onEvent(PostScreenEvent.DislikeButtonClicked)
                                    },
                                    count = state.dislikeCount,
                                    checkedIcon = Icons.Filled.HeartBroken,
                                    unCheckedIcon = Icons.Outlined.HeartBroken
                                )
                                IconButtonWithText(
                                    enabled = false,
                                    unCheckedIcon = Icons.Outlined.Comment,
                                    count = state.commentCount
                                )

                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Comments",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    item {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
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
                                        viewModel.onEvent(
                                            PostScreenEvent.CommentAuthorClicked(
                                                item.author._id
                                            )
                                        )
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
                                var expanded by remember { mutableStateOf(false) }
                                    if (state.isAuthorUser || item.author._id == state.selfId) {
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
                                            DropdownMenuItem(text = { Text("Delete") }, onClick = {
                                                expanded = false
                                                viewModel.onEvent(
                                                    PostScreenEvent.DeleteCommentClicked(
                                                        item._id
                                                    )
                                                )
                                            }, leadingIcon = {
                                                Icon(
                                                    Icons.Outlined.Delete, contentDescription = null
                                                )
                                            })
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = item.detail,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Divider()
                    }
                }
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun IconButtonPreview() {
    IconButtonWithText(
        checked = true,
        onCheckedChange = {

        },
        count = 0,
        checkedIcon = Icons.Filled.Favorite,
        unCheckedIcon = Icons.Filled.FavoriteBorder
    )
}

@Composable
fun IconButtonWithText(
    modifier: Modifier = Modifier,
    checked: Boolean = false,
    onCheckedChange: (Boolean) -> Unit = {},
    count: Int = 0,
    unCheckedIcon: ImageVector,
    checkedIcon: ImageVector = unCheckedIcon,
    enabled: Boolean = true
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Transparent)
            .toggleable(
                value = checked,
                onValueChange = onCheckedChange,
                enabled = enabled,
                role = Role.Checkbox
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(12.dp)) {
            val contentColor = if (checked) {
                MaterialTheme.colorScheme.error
            } else {
                LocalContentColor.current
            }
            CompositionLocalProvider(LocalContentColor provides contentColor) {
                Icon(
                    if (checked) {
                        checkedIcon
                    } else {
                        unCheckedIcon
                    }, contentDescription = ""
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = count.toString())
        }
    }
}
