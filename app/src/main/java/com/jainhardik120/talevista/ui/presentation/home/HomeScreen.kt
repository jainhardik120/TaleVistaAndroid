package com.jainhardik120.talevista.ui.presentation.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
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
        Column(
            Modifier.fillMaxSize()
        ) {
            NavHost(
                navController = navController,
                route = "home_graph",
                startDestination = HomeScreenRoutes.PostsScreen.route
            ) {
                composable(route = HomeScreenRoutes.PostsScreen.route) {
                    val postsScreenViewModel: PostsScreenViewModel = hiltViewModel()
                    PostsScreen(postsScreenViewModel, navController)
                }
                composable(
                    route = HomeScreenRoutes.ProfileScreen.route + "/{userId}", arguments = listOf(
                        navArgument("userId") {
                            type = NavType.StringType
                            nullable = false
                        }
                    )
                ) {
                    val profileScreenViewModel: ProfileScreenViewModel = hiltViewModel()
                    ProfileScreen(profileScreenViewModel, navController)
                }
                composable(route = HomeScreenRoutes.SearchScreen.route) {

                }
                composable(route = HomeScreenRoutes.CreatePostScreen.route) {
                    val createPostsScreenViewModel: CreatePostViewModel = hiltViewModel()
                    CreatePostScreen(
                        createPostsScreenViewModel,
                        viewModel.state,
                        hostState,
                        navController
                    )
                }
                composable(
                    route = HomeScreenRoutes.SinglePostScreen.route + "/{postId}",
                    arguments = listOf(navArgument("postId") {
                        type = NavType.StringType
                        nullable = false
                    })
                ) {
                    val postViewModel: PostViewModel = hiltViewModel()
                    PostScreen(postViewModel, navController)
                }
            }
        }

}

sealed class HomeScreenRoutes(val route: String) {
    object PostsScreen : HomeScreenRoutes("posts_screen")
    object ProfileScreen : HomeScreenRoutes("profile_screen")
    object CreatePostScreen : HomeScreenRoutes("post_screen")
    object SearchScreen : HomeScreenRoutes("search_screen")
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