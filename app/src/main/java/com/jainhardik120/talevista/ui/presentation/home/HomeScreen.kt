package com.jainhardik120.talevista.ui.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jainhardik120.talevista.ui.presentation.home.createpost.CreatePostScreen
import com.jainhardik120.talevista.ui.presentation.home.createpost.CreatePostViewModel
import com.jainhardik120.talevista.ui.presentation.home.posts.PostsScreen
import com.jainhardik120.talevista.ui.presentation.home.posts.PostsScreenViewModel
import com.jainhardik120.talevista.ui.presentation.home.postscreen.PostScreen
import com.jainhardik120.talevista.ui.presentation.home.postscreen.PostViewModel
import com.jainhardik120.talevista.ui.presentation.home.profile.ProfileScreen
import com.jainhardik120.talevista.ui.presentation.home.profile.ProfileScreenViewModel
import com.jainhardik120.talevista.util.UiEvent


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel(), navigateUp: (UiEvent.Navigate) -> Unit) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val createPostsScreenViewModel: CreatePostViewModel = hiltViewModel()

    val hostState = remember { SnackbarHostState() }
    LaunchedEffect(key1 = true, block = {
        viewModel.uiEvent.collect {
            when (it) {
                is UiEvent.Navigate -> {

                }

                is UiEvent.ShowSnackbar -> {
                    hostState.showSnackbar(it.message)
                }
            }
        }
    })
    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = { Text(text = "TaleVista") })
    }, snackbarHost = { SnackbarHost(hostState = hostState) }, bottomBar = {
        val bottomBarScreens = listOf(
            BottomBarScreen.Posts,
            BottomBarScreen.Search,
            BottomBarScreen.Create,
            BottomBarScreen.Chat,
            BottomBarScreen.Profile
        )
        AnimatedVisibility(
            visible = currentDestination?.hierarchy?.any {
                it.route?.contains("bottom_") ?: false
            } == true,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            NavigationBar(Modifier.animateContentSize()) {
                bottomBarScreens.forEachIndexed { _, screen ->
                    NavigationBarItem(selected = currentDestination?.hierarchy?.any {
                        it.route?.contains(screen.route) ?: false
                    } == true, onClick = {
                        navController.navigate(screen.route)
                    }, icon = {
                        Icon(screen.icon, contentDescription = screen.title)
                    }, label = {
                        Text(
                            text = screen.title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    })
                }
            }
        }
    }
    ) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            NavHost(
                navController = navController,
                route = "home_graph",
                startDestination = HomeScreenRoutes.PostsScreen.route
            ) {
                composable(route = HomeScreenRoutes.PostsScreen.route) {
                    val postsScreenViewModel: PostsScreenViewModel = hiltViewModel()
                    PostsScreen(postsScreenViewModel) { route ->
                        navController.navigate(route)
                    }
                }
                composable(
                    route = HomeScreenRoutes.ProfileScreen.route
                ) {
                    val profileScreenViewModel: ProfileScreenViewModel = hiltViewModel()
                    ProfileScreen(profileScreenViewModel)
                }
                composable(route = HomeScreenRoutes.SearchScreen.route) {

                }
                composable(route = HomeScreenRoutes.ChatScreen.route) {

                }
                composable(route = HomeScreenRoutes.CreatePostScreen.route) {
                    CreatePostScreen(createPostsScreenViewModel, viewModel.state, hostState)
                }
                composable(
                    route = HomeScreenRoutes.SinglePostScreen.route + "/{postId}",
                    arguments = listOf(navArgument("postId") {
                        type = NavType.StringType
                        nullable = false
                    })
                ) {
                    val postViewModel: PostViewModel = hiltViewModel()
                    PostScreen(postViewModel)
                }
            }
        }
    }
}

sealed class HomeScreenRoutes(val route: String) {
    object PostsScreen : HomeScreenRoutes("bottom_posts_screen")
    object ProfileScreen : HomeScreenRoutes("bottom_profile_screen")
    object CreatePostScreen : HomeScreenRoutes("create_post_screen")
    object ChatScreen : HomeScreenRoutes("bottom_chat_screen")
    object SearchScreen : HomeScreenRoutes("bottom_search_screen")
    object SinglePostScreen : HomeScreenRoutes("single_post_screen")

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}

sealed class BottomBarScreen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Posts : BottomBarScreen(
        route = HomeScreenRoutes.PostsScreen.route,
        title = "Home",
        icon = Icons.Filled.Home
    )

    object Profile : BottomBarScreen(
        route = HomeScreenRoutes.ProfileScreen.route,
        title = "Profile",
        icon = Icons.Filled.Person
    )

    object Create : BottomBarScreen(
        route = HomeScreenRoutes.CreatePostScreen.route,
        title = "Create",
        icon = Icons.Filled.Add
    )

    object Chat : BottomBarScreen(
        route = HomeScreenRoutes.ChatScreen.route,
        title = "Chat",
        icon = Icons.Filled.Chat
    )

    object Search : BottomBarScreen(
        route = HomeScreenRoutes.SearchScreen.route,
        title = "Search",
        icon = Icons.Filled.Explore
    )
}

