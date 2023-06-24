package com.jainhardik120.talevista.ui.presentation.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jainhardik120.talevista.ui.presentation.Screen
import com.jainhardik120.talevista.util.UiEvent

@Composable
fun LoginScreen(viewModel: LoginViewModel = hiltViewModel(), navigateUp : (String)->Unit) {
    val hostState = remember { SnackbarHostState() }
    val navController = rememberNavController()
    LaunchedEffect(key1 = true, block = {
        viewModel.uiEvent.collect{
            when(it){
                is UiEvent.Navigate -> {
                    if (it.route == Screen.HomeScreen.route) {
                        navigateUp(it.route)
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

    Scaffold(snackbarHost = { SnackbarHost(hostState = hostState) }) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            NavHost(
                navController = navController,
                startDestination = LoginScreenRoutes.EmailLoginScreen.route,
                route = "login_graph"
            ) {
                composable(route = LoginScreenRoutes.EmailLoginScreen.route) {
                    EmailLoginScreen(
                        onEvent = viewModel::onEvent,
                        state = viewModel.state,
                        handleIntentResult = viewModel::handleIntentResult
                    )
                }
                composable(route = LoginScreenRoutes.RegisterMailScreen.route) {
                    RegisterMailScreen(onEvent = viewModel::onEvent, state = viewModel.state)
                }
                composable(route = LoginScreenRoutes.RegisterPasswordScreen.route) {
                    RegisterPasswordScreen(onEvent = viewModel::onEvent, state = viewModel.state)
                }
                composable(route = LoginScreenRoutes.RegisterUsernameScreen.route) {
                    RegisterUsernameScreen(
                        onEvent = viewModel::onEvent,
                        state = viewModel.state,
                        isGoogle = false
                    )
                }
                composable(route = LoginScreenRoutes.GoogleUsernameScreen.route) {
                    RegisterUsernameScreen(
                        onEvent = viewModel::onEvent,
                        state = viewModel.state,
                        isGoogle = true
                    )
                }
            }
        }
    }
}


sealed class LoginScreenRoutes(val route: String){
    object EmailLoginScreen : LoginScreenRoutes("email_login")
    object RegisterMailScreen : LoginScreenRoutes("register_mail")
    object RegisterPasswordScreen : LoginScreenRoutes("register_password")
    object RegisterUsernameScreen : LoginScreenRoutes("register_username")
    object GoogleUsernameScreen : LoginScreenRoutes("google_username")
}