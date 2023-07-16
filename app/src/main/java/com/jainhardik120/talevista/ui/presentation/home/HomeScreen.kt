package com.jainhardik120.talevista.ui.presentation.home

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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
import com.jainhardik120.talevista.ui.presentation.home.search.SearchScreen
import com.jainhardik120.talevista.ui.presentation.home.search.SearchViewModel
import com.jainhardik120.talevista.util.UiEvent


@Composable
fun HomeScreen(navigateUp: (UiEvent.Navigate) -> Unit) {
    val navController = rememberNavController()
    val searchViewModel = hiltViewModel<SearchViewModel>()
    Column(
        Modifier.fillMaxSize()
    ) {
        NavHost(
            navController = navController,
            route = "home_graph",
            startDestination = HomeScreenRoutes.PostsScreen.route, enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(700)
                )
            },
            exitTransition = {
                if (targetState.destination.route?.contains(HomeScreenRoutes.CreatePostScreen.route) == true) {
                    ExitTransition.None
                } else {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(700)
                    )
                }
            },
            popEnterTransition = {
                if (initialState.destination.route?.contains(HomeScreenRoutes.CreatePostScreen.route) == true) {
                    EnterTransition.None
                } else {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(700)
                    )
                }
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(700)
                )
            }
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
                ProfileScreen(profileScreenViewModel, navController, navigateUp)
            }
            composable(route = HomeScreenRoutes.SearchScreen.route) {
                SearchScreen(viewModel = searchViewModel, navController = navController)
            }
            composable(
                route = HomeScreenRoutes.CreatePostScreen.route + "?postId={postId}",
                arguments = listOf(
                    navArgument("postId") {
                        type = NavType.StringType
                        nullable = true
                    }
                ),
                enterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Up,
                        animationSpec = tween(700)
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(700)
                    )
                },
                popEnterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(700)
                    )
                },
                popExitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Down,
                        animationSpec = tween(700)
                    )
                }
            ) {
                val createPostsScreenViewModel: CreatePostViewModel = hiltViewModel()
                CreatePostScreen(
                    createPostsScreenViewModel,
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
    object CreatePostScreen : HomeScreenRoutes("create_post_screen")
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