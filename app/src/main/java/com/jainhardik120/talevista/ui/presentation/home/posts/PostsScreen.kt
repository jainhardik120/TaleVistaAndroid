package com.jainhardik120.talevista.ui.presentation.home.posts

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Create
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.jainhardik120.talevista.R
import com.jainhardik120.talevista.ui.components.CustomLargeAppBar
import com.jainhardik120.talevista.ui.components.CustomScrollableTabRow
import com.jainhardik120.talevista.ui.components.PaginatingColumn
import com.jainhardik120.talevista.ui.components.PostCardEvent
import com.jainhardik120.talevista.ui.presentation.home.HomeScreenRoutes
import com.jainhardik120.talevista.util.UiEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostsScreen(viewModel: PostsScreenViewModel, navController: NavController) {
    val hostState = remember { SnackbarHostState() }
    LaunchedEffect(key1 = Unit, block = {
        viewModel.loadList()
        viewModel.uiEvent.collect {
            when (it) {
                is UiEvent.Navigate -> {
                    navController.navigate(it.route)
                }

                is UiEvent.ShowSnackbar -> {
                    hostState.showSnackbar(it.message)
                }
            }
        }
    })
    val lazyListState = rememberLazyListState()
    val shouldStartPaginate = remember {
        derivedStateOf {
            viewModel.canPaginate && (lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
                ?: -9) >= (lazyListState.layoutInfo.totalItemsCount - 6)
        }
    }
    LaunchedEffect(key1 = shouldStartPaginate.value, block = {
        if (shouldStartPaginate.value && viewModel.listState == ListState.IDLE) {
            viewModel.loadList()
        }
    })
    val posts by viewModel.posts.collectAsState()
    val topAppBarScrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior()


    fun HomePost.event(index: Int, event: PostCardEvent) {
        viewModel.onEvent(PostsScreenEvent.CardEvent(event, this, index = index))
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = hostState) },
        topBar = {
            CustomLargeAppBar(
                upperBar = {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier
                            .size(40.dp)
                            .padding(4.dp)
                            .clip(RoundedCornerShape(100))
                            .clickable {
                                viewModel.onEvent(PostsScreenEvent.ProfileLogoClicked)
                            }) {
                            AsyncImage(
                                model = viewModel.state.profileImageUrl,
                                contentDescription = "ProfileIcon"
                            )
                        }
                        Row(
                            Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.app_name),
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                        IconButton(onClick = {
                            navController.navigate(HomeScreenRoutes.SearchScreen.route)
                        }) {
                            Icon(Icons.Rounded.Search, contentDescription = "Search Icon")
                        }
                    }
                },
                lowerBar = {
                    if (viewModel.state.categories.isNotEmpty()) {
                        CustomScrollableTabRow(
                            tabs = viewModel.state.categories.map { categoriesItem ->
                                categoriesItem.name
                            },
                            selectedTabIndex = viewModel.state.selectedCategoryIndex,
                            onTabClick = {
                                viewModel.onEvent(PostsScreenEvent.TabChanged(it))
                            }
                        )
                    }
                },
                lowerBarHeight = 48.dp,
                upperBarHeight = 64.dp,
                scrollBehavior = topAppBarScrollBehavior
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate(HomeScreenRoutes.CreatePostScreen.route)
            }) {
                Icon(Icons.Rounded.Create, contentDescription = "Create Icon")
            }
        }
    ) { paddingValues ->
        PaginatingColumn(
            listState = viewModel.listState,
            lazyListState = lazyListState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            items = {
                itemsIndexed(posts, key = { _, item ->
                    item._id
                }) { index, item ->
                    Column(
                        Modifier
                            .clickable {
                                item.event(index, PostCardEvent.PostClicked)
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
                                    item.event(index, PostCardEvent.AuthorClicked)
                                }) {
                                AsyncImage(
                                    model = item.author.picture,
                                    contentDescription = "ProfileIcon"
                                )
                            }
                            Column(
                                modifier = Modifier
                                    .padding(horizontal = 12.dp, vertical = 4.dp),
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
                                if (viewModel.state.selectedCategoryIndex == 0) {
                                    Text(text = item.category, modifier = Modifier.clickable {
                                        viewModel.onEvent(PostsScreenEvent.TabChanged(item.categoryIndex))
                                    }, style = MaterialTheme.typography.labelMedium)
                                }

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
            },
            loadingItem = {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    CircularProgressIndicator(Modifier.padding(16.dp))
                }
            }
        )
    }
}
