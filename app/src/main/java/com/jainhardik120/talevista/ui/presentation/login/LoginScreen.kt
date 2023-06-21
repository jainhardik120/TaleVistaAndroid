package com.jainhardik120.talevista.ui.presentation.login

import android.util.Log
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
                    Log.d("TAG", "LoginScreen: Recieved Snackbar")
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

//    Scaffold(snackbarHost = { SnackbarHost(hostState = hostState) }) { padding ->
//        Column(Modifier.padding(padding), verticalArrangement = Arrangement.Center) {
//            when (viewModel.state.currPage) {
//                LoginPage.ExistingAccount -> {
//
//                    }
//                }
//
//                LoginPage.NewAccount -> {
//                    Column {
//                        val showPassword = rememberSaveable { mutableStateOf(false) }
//                        OutlinedTextField(
//                            value = state.registerFName,
//                            onValueChange = { viewModel.onEvent(LoginEvent.RegisterFNameChanged(it)) },
//                            label = {
//                                Text(text = "First Name")
//                            },
//                            textStyle = MaterialTheme.typography.bodyMedium,
//                            keyboardOptions = KeyboardOptions.Default.copy(
//                                imeAction = ImeAction.Next,
//                                keyboardType = KeyboardType.Text
//                            ),
//                            keyboardActions = KeyboardActions(
//                                onDone = {
//
//                                }
//                            ),
//                            singleLine = true
//                        )
//                        Spacer(modifier = Modifier.height(8.dp))
//                        OutlinedTextField(
//                            value = state.registerLName,
//                            onValueChange = { viewModel.onEvent(LoginEvent.RegisterLNameChanged(it)) },
//                            label = {
//                                Text(text = "Last Name")
//                            },
//                            textStyle = MaterialTheme.typography.bodyMedium,
//                            keyboardOptions = KeyboardOptions.Default.copy(
//                                imeAction = ImeAction.Next,
//                                keyboardType = KeyboardType.Text
//                            ),
//                            keyboardActions = KeyboardActions(
//                                onDone = {
//
//                                }
//                            ),
//                            singleLine = true
//                        )
//                        Spacer(modifier = Modifier.height(8.dp))
//                        OutlinedTextField(
//                            value = state.registerEmail,
//                            onValueChange = { viewModel.onEvent(LoginEvent.RegisterMailChanged(it)) },
//                            label = {
//                                Text(text = "Email")
//                            },
//                            textStyle = MaterialTheme.typography.bodyMedium,
//                            keyboardOptions = KeyboardOptions.Default.copy(
//                                imeAction = ImeAction.Next,
//                                keyboardType = KeyboardType.Email
//                            ),
//                            keyboardActions = KeyboardActions(
//                                onDone = {
//
//                                }
//                            ),
//                            singleLine = true
//                        )
//                        Spacer(modifier = Modifier.height(8.dp))
//                        OutlinedTextField(
//                            modifier = Modifier.fillMaxWidth(),
//                            value = state.registerPassword,
//                            label = {
//                                Text(
//                                    text = "Password",
//                                    style = MaterialTheme.typography.bodyMedium
//                                )
//                            },
//                            onValueChange = {
//                                viewModel.onEvent(LoginEvent.RegisterPasswordChanged(it))
//                            },
//                            textStyle = MaterialTheme.typography.bodyMedium,
//                            trailingIcon = {
//                                if (showPassword.value) {
//                                    IconButton(onClick = { showPassword.value = false }) {
//                                        Icon(
//                                            imageVector = Icons.Filled.Visibility,
//                                            contentDescription = "Hide Password"
//                                        )
//                                    }
//                                } else {
//                                    IconButton(onClick = { showPassword.value = true }) {
//                                        Icon(
//                                            imageVector = Icons.Filled.VisibilityOff,
//                                            contentDescription = "Show Password"
//                                        )
//                                    }
//                                }
//                            }, visualTransformation = if (showPassword.value) {
//                                VisualTransformation.None
//                            } else {
//                                PasswordVisualTransformation()
//                            },
//                            keyboardOptions = KeyboardOptions.Default.copy(
//                                imeAction = ImeAction.Done,
//                                keyboardType = KeyboardType.Password
//                            ),
//                            keyboardActions = KeyboardActions(
//                                onDone = {
//
//                                }
//                            ),
//                            singleLine = true
//                        )
//                        Spacer(modifier = Modifier.height(8.dp))
//                        Button(onClick = {
//                            viewModel.onEvent(LoginEvent.RegisterButtonClicked)
//                        }) {
//                            Text(text = "Register")
//                        }
//
//                        TextButton(onClick = { viewModel.onEvent(LoginEvent.ChangeScreen) }) {
//                            Text(text = "Already have an account? Login")
//                        }
//                        Spacer(modifier = Modifier.height(8.dp))
//                    }
//                }
//            }
//        }
//    }
}


sealed class LoginScreenRoutes(val route: String){
    object EmailLoginScreen : LoginScreenRoutes("email_login")
    object RegisterMailScreen : LoginScreenRoutes("register_mail")
    object RegisterPasswordScreen : LoginScreenRoutes("register_password")
    object RegisterUsernameScreen : LoginScreenRoutes("register_username")
    object GoogleUsernameScreen : LoginScreenRoutes("google_username")

    fun withArgs(vararg args: String):String{
        return buildString {
            append(route)
            args.forEach { arg->
                append("/$arg")
            }
        }
    }
}