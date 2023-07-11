package com.jainhardik120.talevista.ui.presentation.home.posts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Create
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.jainhardik120.talevista.ui.components.CustomLargeAppBar
import com.jainhardik120.talevista.ui.components.CustomScrollableTabRow
import com.jainhardik120.talevista.ui.components.PaginatingColumn
import com.jainhardik120.talevista.ui.components.PostCard
import com.jainhardik120.talevista.ui.presentation.home.HomeScreenRoutes
import com.jainhardik120.talevista.util.UiEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostsScreen(viewModel: PostsScreenViewModel, navController: NavController) {
    LaunchedEffect(key1 = Unit, block = {
        viewModel.getPosts()
        viewModel.uiEvent.collect {
            when (it) {
                is UiEvent.Navigate -> {
                    navController.navigate(it.route)
                }

                is UiEvent.ShowSnackbar -> {

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
            viewModel.getPosts()
        }
    })
    val posts by viewModel.posts.collectAsState()
    val topAppBarScrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        topBar = {
            CustomLargeAppBar(
                upperBar = {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            viewModel.onEvent(PostsScreenEvent.ProfileLogoClicked)
                        }) {
                            Box(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .clip(RoundedCornerShape(100))
                            ) {
                                AsyncImage(
                                    model = viewModel.state.profileImageUrl,
                                    contentDescription = "ProfileIcon"
                                )
                            }
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
            data = posts,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            item = { item, index ->
                PostCard(post = item, onEvent = {
                    viewModel.onEvent(PostsScreenEvent.CardEvent(it, item, index))
                }, index = index)
            }
        )
    }
}

