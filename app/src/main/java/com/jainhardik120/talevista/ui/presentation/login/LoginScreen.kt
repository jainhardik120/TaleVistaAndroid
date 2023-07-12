package com.jainhardik120.talevista.ui.presentation.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jainhardik120.talevista.R
import com.jainhardik120.talevista.ui.presentation.Screen
import com.jainhardik120.talevista.ui.presentation.login.components.EmailLoginScreen
import com.jainhardik120.talevista.ui.presentation.login.components.ForgotPasswordScreen
import com.jainhardik120.talevista.ui.presentation.login.components.RegisterMailScreen
import com.jainhardik120.talevista.ui.presentation.login.components.RegisterPasswordScreen
import com.jainhardik120.talevista.ui.presentation.login.components.RegisterUsernameScreen
import com.jainhardik120.talevista.util.NAVIGATE_UP_ROUTE
import com.jainhardik120.talevista.util.UiEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(viewModel: LoginViewModel = hiltViewModel(), navigateUp: (String) -> Unit) {
    val hostState = remember { SnackbarHostState() }
    val navController = rememberNavController()
    LaunchedEffect(key1 = true, block = {
        viewModel.uiEvent.collect {
            when (it) {
                is UiEvent.Navigate -> {
                    if (it.route == Screen.HomeScreen.route) {
                        navigateUp(it.route)
                    } else if (it.route == NAVIGATE_UP_ROUTE) {
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


    val username by viewModel.username.collectAsState()
    val usernameAvailable by viewModel.usernameAvailable.collectAsState()


    Column(
        Modifier.imePadding()
    ) {
        Scaffold(snackbarHost = { SnackbarHost(hostState = hostState) }, topBar = {
            CenterAlignedTopAppBar(title = {
                Text(text = stringResource(id = R.string.app_name))
            })
        }) { paddingValues ->
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
                        RegisterPasswordScreen(
                            onEvent = viewModel::onEvent,
                            state = viewModel.state
                        )
                    }
                    composable(route = LoginScreenRoutes.RegisterUsernameScreen.route) {
                        RegisterUsernameScreen(
                            onEvent = viewModel::onEvent,
                            state = viewModel.state,
                            username,
                            usernameAvailable
                        )
                    }
                    composable(route = LoginScreenRoutes.ForgotPasswordScreen.route) {
                        ForgotPasswordScreen(onEvent = viewModel::onEvent, state = viewModel.state)
                    }
                }
            }
        }
    }
}


sealed class LoginScreenRoutes(val route: String) {
    object EmailLoginScreen : LoginScreenRoutes("email_login")
    object RegisterMailScreen : LoginScreenRoutes("register_mail")
    object RegisterPasswordScreen : LoginScreenRoutes("register_password")
    object RegisterUsernameScreen : LoginScreenRoutes("register_username")
    object ForgotPasswordScreen : LoginScreenRoutes("forgot_email")
}