package com.jainhardik120.talevista.ui.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jainhardik120.talevista.ui.presentation.home.createpost.CreatePostScreen
import com.jainhardik120.talevista.ui.presentation.home.createpost.CreatePostViewModel
import com.jainhardik120.talevista.ui.presentation.home.posts.PostsScreen
import com.jainhardik120.talevista.ui.presentation.home.posts.PostsScreenViewModel
import com.jainhardik120.talevista.util.UiEvent

@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel(), navigateUp: (UiEvent.Navigate) -> Unit) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val postsScreenViewModel: PostsScreenViewModel = hiltViewModel()
    val createPostsScreenViewModel: CreatePostViewModel = hiltViewModel()
    val hostState = remember { SnackbarHostState() }
    Scaffold(snackbarHost = { SnackbarHost(hostState = hostState) }, bottomBar = {
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
    },
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.exclude(WindowInsets.navigationBars)
            .exclude(WindowInsets.ime)
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
                    PostsScreen(postsScreenViewModel)
                }
                composable(route = HomeScreenRoutes.ProfileScreen.route) {
                }
                composable(route = HomeScreenRoutes.SearchScreen.route) {

                }
                composable(route = HomeScreenRoutes.ChatScreen.route) {

                }
                composable(route = HomeScreenRoutes.CreatePostScreen.route) {
                    CreatePostScreen(createPostsScreenViewModel, viewModel.state, hostState)
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

