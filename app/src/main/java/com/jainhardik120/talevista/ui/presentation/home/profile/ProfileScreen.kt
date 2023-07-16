package com.jainhardik120.talevista.ui.presentation.home.profile

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Report
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.jainhardik120.talevista.R
import com.jainhardik120.talevista.data.remote.dto.User
import com.jainhardik120.talevista.ui.components.PaginatingColumn
import com.jainhardik120.talevista.ui.presentation.Screen
import com.jainhardik120.talevista.ui.presentation.home.posts.ListState
import com.jainhardik120.talevista.util.NAVIGATE_LOGIN_ROUTE
import com.jainhardik120.talevista.util.UiEvent
import kotlinx.coroutines.launch


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileScreenViewModel,
    navController: NavHostController,
    navigateUp: (UiEvent.Navigate) -> Unit
) {
    val hostState = remember { SnackbarHostState() }
    LaunchedEffect(key1 = Unit, block = {
        viewModel.init()
        viewModel.uiEvent.collect {
            when (it) {
                is UiEvent.Navigate -> {
                    if (it.route == NAVIGATE_LOGIN_ROUTE) {
                        navigateUp(UiEvent.Navigate(Screen.LoginScreen.route))
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
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f
    ) {
        3
    }
    val tabItems = listOf(
        "Posts",
        "Likes",
        "Comments"
    )
    val scope = rememberCoroutineScope()

    val lazyListState = rememberLazyListState()
    val shouldStartPaginate = remember {
        derivedStateOf {
            viewModel.canPaginate && (lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
                ?: -9) >= (lazyListState.layoutInfo.totalItemsCount - 6)
        }
    }
    LaunchedEffect(key1 = shouldStartPaginate.value, block = {
        if (shouldStartPaginate.value && viewModel.listState == ListState.IDLE) {
            viewModel.getPosts()
        }
    })

    val posts by viewModel.posts.collectAsState()

    val likedLazyListState = rememberLazyListState()
    val likedShouldPaginate = remember {
        derivedStateOf {
            viewModel.likedCanPaginate && (likedLazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
                ?: -9) >= (likedLazyListState.layoutInfo.totalItemsCount - 6)
        }
    }
    LaunchedEffect(key1 = likedShouldPaginate.value, block = {
        if (likedShouldPaginate.value && viewModel.likedListState == ListState.IDLE) {
            viewModel.getLikedPosts()
        }
    })

    val likedPosts by viewModel.likedPosts.collectAsState()


    val commentLazyListState = rememberLazyListState()
    val commentShouldPaginate = remember {
        derivedStateOf {
            viewModel.commentCanPaginate && (commentLazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
                ?: -9) >= (commentLazyListState.layoutInfo.totalItemsCount - 6)
        }
    }
    LaunchedEffect(key1 = commentShouldPaginate.value, block = {
        if (commentShouldPaginate.value && viewModel.commentListState == ListState.IDLE) {
            viewModel.getComments()
        }
    })

    val comments by viewModel.comments.collectAsState()


    Scaffold(snackbarHost = { SnackbarHost(hostState = hostState) }) { paddingValues ->
        BoxWithConstraints(Modifier.padding(paddingValues)) {
            val screenHeight = maxHeight
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(state = scrollState)
            ) {
                Column {
                    Row(
                        Modifier
                            .padding(10.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(onClick = {
                            scope.launch {
                                navController.navigateUp()
                            }
                        }) {
                            Icon(Icons.Rounded.ArrowBack, contentDescription = "Back Arrow")
                        }
                        Text(
                            modifier = Modifier.padding(start = 10.dp),
                            text = state.user?.user?.username ?: stringResource(R.string.app_name),
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        if (state.isSelfUser) {
                            Spacer(modifier = Modifier.width(10.dp))
                            Spacer(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                            )
                            Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
                                IconButton(onClick = { viewModel.onEvent(ProfileScreenEvent.MoreIconClicked) }) {
                                    Icon(
                                        Icons.Default.MoreVert,
                                        contentDescription = "Localized description"
                                    )
                                }
                                DropdownMenu(
                                    expanded = viewModel.state.menuExpanded,
                                    onDismissRequest = { viewModel.onEvent(ProfileScreenEvent.DismissMenu) }
                                ) {

                                    DropdownMenuItem(
                                        text = { Text("Logout") },
                                        onClick = {
                                            viewModel.onEvent(ProfileScreenEvent.DismissMenu)
                                            viewModel.onEvent(ProfileScreenEvent.LogoutItemClicked)
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

                    }
                    if (state.user != null) {
                        ProfileHeader(isSelfUser = state.isSelfUser, user = state.user)
                    }
                }
                Column(modifier = Modifier.height(screenHeight)) {
                    TabRow(
                        selectedTabIndex = pagerState.currentPage,
                        divider = { Divider() },
                        indicator = { tabPositions ->
                            TabRowDefaults.Indicator(
                                Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage])
                            )
                        }
                    ) {
                        tabItems.forEachIndexed { i, label ->
                            Tab(
                                selected = pagerState.currentPage == i,
                                onClick = {
                                    scope.launch {
                                        pagerState.animateScrollToPage(i)
                                    }
                                },
                                text = {
                                    Text(text = label)
                                }
                            )
                        }
                    }
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxHeight()
                            .nestedScroll(
                                remember {
                                    object : NestedScrollConnection {
                                        override fun onPreScroll(
                                            available: Offset,
                                            source: NestedScrollSource
                                        ): Offset {
                                            return if (available.y > 0) Offset.Zero else Offset(
                                                x = 0f,
                                                y = -scrollState.dispatchRawDelta(-available.y)
                                            )
                                        }
                                    }
                                }
                            )
                    ) { page ->
                        when (page) {
                            0 -> {
                                PaginatingColumn(
                                    listState = viewModel.listState,
                                    lazyListState = lazyListState,
                                    items = {
                                        itemsIndexed(posts, key = { _, item ->
                                            item._id
                                        }) { index, item ->
                                            Column(
                                                Modifier
                                                    .clickable {
                                                        viewModel.onEvent(
                                                            ProfileScreenEvent.PostClicked(
                                                                item._id
                                                            )
                                                        )
                                                    }
                                                    .padding(16.dp)
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
                                                                ProfileScreenEvent.UserIconClicked(
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
                                                        modifier = Modifier
                                                            .padding(
                                                                horizontal = 12.dp,
                                                                vertical = 4.dp
                                                            ),
                                                        verticalArrangement = Arrangement.Center
                                                    ) {
                                                        Text(
                                                            text = item.author.username,
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
                                                    Column(verticalArrangement = Arrangement.Center) {
                                                        Text(
                                                            text = item.category,
                                                            style = MaterialTheme.typography.labelMedium
                                                        )
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text(
                                                    text = item.content,
                                                    overflow = TextOverflow.Ellipsis,
                                                    modifier = Modifier.fillMaxWidth(),
                                                    maxLines = 4
                                                )

                                            }
                                            Divider()
                                        }
                                    }
                                )
                            }

                            1 -> {
                                PaginatingColumn(
                                    listState = viewModel.likedListState,
                                    lazyListState = likedLazyListState,
                                    items = {
                                        itemsIndexed(likedPosts, key = { _, item ->
                                            item._id
                                        }) { index, item ->
                                            Column(
                                                Modifier
                                                    .clickable {
                                                        viewModel.onEvent(
                                                            ProfileScreenEvent.PostClicked(
                                                                item._id
                                                            )
                                                        )
                                                    }
                                                    .padding(16.dp)
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
                                                                ProfileScreenEvent.UserIconClicked(
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
                                                        modifier = Modifier
                                                            .padding(
                                                                horizontal = 12.dp,
                                                                vertical = 4.dp
                                                            ),
                                                        verticalArrangement = Arrangement.Center
                                                    ) {
                                                        Text(
                                                            text = item.author.username,
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
                                                    Column(verticalArrangement = Arrangement.Center) {
                                                        Text(
                                                            text = item.category,
                                                            style = MaterialTheme.typography.labelMedium
                                                        )
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text(
                                                    text = item.content,
                                                    overflow = TextOverflow.Ellipsis,
                                                    modifier = Modifier.fillMaxWidth(),
                                                    maxLines = 4
                                                )

                                            }
                                            Divider()
                                        }
                                    }
                                )
                            }

                            2 -> {
                                PaginatingColumn(
                                    listState = viewModel.commentListState,
                                    lazyListState = commentLazyListState,
                                    items = {
                                        itemsIndexed(comments, key = { _, item ->
                                            item._id
                                        }) { _, item ->
                                            Column(
                                                Modifier
                                                    .clickable {
                                                        viewModel.onEvent(
                                                            ProfileScreenEvent.PostClicked(
                                                                item.postId
                                                            )
                                                        )
                                                    }
                                                    .padding(16.dp)
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth(),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Box(modifier = Modifier
                                                        .size(40.dp)
                                                        .padding(4.dp)
                                                        .clip(RoundedCornerShape(100))
                                                        .clickable {
                                                            viewModel.onEvent(
                                                                ProfileScreenEvent.UserIconClicked(
                                                                    item.postAuthorId
                                                                )
                                                            )
                                                        }) {
                                                        AsyncImage(
                                                            model = item.postAuthorPicture,
                                                            contentDescription = "ProfileIcon"
                                                        )
                                                    }
                                                    Column(
                                                        modifier = Modifier
                                                            .padding(
                                                                horizontal = 12.dp,
                                                                vertical = 4.dp
                                                            )
                                                            .weight(1f)
                                                            .fillMaxWidth(),
                                                        verticalArrangement = Arrangement.Center
                                                    ) {
                                                        Text(
                                                            text = "@${item.postAuthorUsername}",
                                                            style = MaterialTheme.typography.labelMedium
                                                        )
                                                        Text(
                                                            text = item.postCreatedAt,
                                                            style = MaterialTheme.typography.labelMedium,
                                                            color = MaterialTheme.colorScheme.outline,
                                                            maxLines = 1
                                                        )
                                                        Text(
                                                            text = item.postContent,
                                                            style = MaterialTheme.typography.labelMedium,
                                                            maxLines = 2
                                                        )
                                                    }
                                                }
                                                Card(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                ) {
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(16.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Column(
                                                            modifier = Modifier
                                                                .padding(
                                                                    vertical = 4.dp
                                                                )
                                                                .weight(1f)
                                                                .fillMaxWidth(),
                                                            verticalArrangement = Arrangement.Center
                                                        ) {
                                                            Text(
                                                                text = item.createdAt,
                                                                style = MaterialTheme.typography.labelMedium,
                                                                color = MaterialTheme.colorScheme.outline,
                                                                maxLines = 1
                                                            )
                                                            Text(
                                                                text = item.commentContent,
                                                                overflow = TextOverflow.Ellipsis,
                                                                modifier = Modifier.fillMaxWidth()
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                            Divider()
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ProfileHeader(isSelfUser: Boolean, user: User) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        AsyncImage(
            model = user.user.picture,
            modifier = Modifier
                .size(100.dp)
                .weight(3f)
                .aspectRatio(1f, matchHeightConstraintsFirst = true)
                .border(width = 1.dp, color = Color.LightGray, shape = CircleShape)
                .padding(3.dp)
                .clip(
                    CircleShape
                ),
            contentDescription = "Profile Icon"
        )
        Spacer(modifier = Modifier.width(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.weight(7f)
        ) {
            ProfileStat(
                numberText = (user.postsCount ?: 0).toString(),
                text = "Posts"
            )
            ProfileStat(
                numberText = (user.likeCount ?: 0).toString(),
                text = "Likes"
            )
            ProfileStat(
                numberText = (user.commentCount ?: 0).toString(),
                text = "Comments"
            )
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        val letterSpacing = 0.5.sp
        val lineHeight = 20.sp
        Text(
            text = "${user.user.first_name ?: ""} ${user.user.last_name ?: ""}",
            fontWeight = FontWeight.Bold,
            letterSpacing = letterSpacing,
            lineHeight = lineHeight
        )
        if (isSelfUser) {
            Text(
                text = user.user.email ?: "",
                letterSpacing = letterSpacing,
                lineHeight = lineHeight
            )
        }
    }
    Spacer(modifier = Modifier.height(25.dp))
}

@Composable
fun ProfileStat(
    numberText: String,
    text: String,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = numberText,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = text)
    }
}