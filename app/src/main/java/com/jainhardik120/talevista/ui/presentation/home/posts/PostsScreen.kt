package com.jainhardik120.talevista.ui.presentation.home.posts

//import androidx.compose.material3.CustomLargeAppBar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Create
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jainhardik120.talevista.ui.components.CustomLargeAppBar
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


    var currentPage by remember {
        mutableStateOf(0)
    }

    val searchText by viewModel.searchText.collectAsState()
    val users by viewModel.users.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    var active by remember { mutableStateOf(false) }

    val posts by viewModel.posts.collectAsState()
    val topAppBarScrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        topBar = {
//            CenterAlignedTopAppBar(
//                title = {
//                    Text(text = "TaleVista")
//                },
//                scrollBehavior = topAppBarScrollBehavior,
//                navigationIcon = {
//                    IconButton(onClick = {
//                        viewModel.onEvent(PostsScreenEvent.ProfileLogoClicked)
//                    }) {
//                        Box(
//                            modifier = Modifier
//                                .padding(4.dp)
//                                .clip(RoundedCornerShape(100))
//                        ) {
//                            AsyncImage(
//                                model = viewModel.state.profileImageUrl,
//                                contentDescription = "ProfileIcon"
//                            )
//                        }
//                    }
//                },
//                actions = {
//                    IconButton(onClick = {
//                        navController.navigate(HomeScreenRoutes.SearchScreen.route)
//                    }) {
//                        Icon(Icons.Rounded.Search, contentDescription = "Search Icon")
//                    }
//                }
//            )
//            
            CustomLargeAppBar(
                upperBar = {
                    SearchBar(modifier = Modifier.fillMaxWidth(),
                        query = searchText,
                        onQueryChange = viewModel::onSearchChanged,
                        onSearch = {

                        },
                        active = active,
                        onActiveChange = { active = it }
                    ) {
                        LazyColumn(modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp), content = {
                                itemsIndexed(users) { index, item ->
                                    Text(text = item.username)
                                }
                            }
                        )
                    }
                },
                lowerBar = {
                    ScrollableTabRow(
                        selectedTabIndex = 0,
                        divider = { Divider() }
                    ) {
                        repeat(10) { it ->
                            Tab(
                                selected = it == currentPage,
                                onClick = {
                                    currentPage = it
                                },
                                text = {
                                    Text(text = "Tab $it")
                                }
                            )
                        }
                    }
                },
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

//
//    Scaffold(
//        topBar = {
//
//        }
//    ) {paddingValues ->
//        BoxWithConstraints(Modifier.padding(paddingValues)) {
//            val screenHeight = maxHeight
//            val scrollState = rememberScrollState()
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .verticalScroll(state = scrollState)
//            ) {
//
//                Column(modifier = Modifier.height(screenHeight)) {
//
//                    Column(
//                        modifier = Modifier
//                            .fillMaxHeight()
//                            .nestedScroll(
//                                remember {
//                                    object : NestedScrollConnection {
//                                        override fun onPreScroll(
//                                            available: Offset,
//                                            source: NestedScrollSource
//                                        ): Offset {
//                                            return if (available.y > 0) Offset.Zero else Offset(
//                                                x = 0f,
//                                                y = -scrollState.dispatchRawDelta(-available.y)
//                                            )
//                                        }
//                                    }
//                                }
//                            )
//                    ) {
//
//                    }
//                }
//            }
//        }
//    }

//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//    ) {
//        Box(modifier = Modifier
//            .semantics { isContainer = true }
//            .zIndex(1f)
//            .fillMaxWidth()) {
//
//        }
//        PaginatingColumn(
//            listState = viewModel.listState,
//            lazyListState = lazyListState,
//            data = posts,
//            modifier = Modifier.fillMaxSize(),
//            item = { item, index ->
//                PostCard(post = item, onEvent = {
//                    viewModel.onEvent(PostsScreenEvent.CardEvent(it, item, index))
//                }, index = index)
//            }
//        )
//    }
}

