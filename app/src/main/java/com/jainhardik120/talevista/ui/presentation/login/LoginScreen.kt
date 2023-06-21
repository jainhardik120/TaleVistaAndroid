package com.jainhardik120.talevista.ui.presentation.login

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun LoginScreen(viewModel: LoginViewModel = hiltViewModel()) {
    val state = viewModel.state
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = viewModel::handleIntentResult
    )

    Scaffold { padding ->
        Column(Modifier.padding(padding), verticalArrangement = Arrangement.Center) {
            when (viewModel.state.currPage) {
                LoginPage.ExistingAccount -> {
                    val showPassword = rememberSaveable { mutableStateOf(false) }
                    Column(
                        Modifier
                            .padding(horizontal = 8.dp)
                            .fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = state.loginEmail,
                            onValueChange = { viewModel.onEvent(LoginEvent.LoginMailChanged(it)) },
                            label = {
                                Text(text = "Email Address")
                            },
                            textStyle = MaterialTheme.typography.bodyMedium,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Next,
                                keyboardType = KeyboardType.Email
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {

                                }
                            ),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = state.loginPassword,
                            label = {
                                Text(
                                    text = "Password",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            onValueChange = {
                                viewModel.onEvent(LoginEvent.LoginPasswordChanged(it))
                            },
                            textStyle = MaterialTheme.typography.bodyMedium,
                            trailingIcon = {
                                if (showPassword.value) {
                                    IconButton(onClick = { showPassword.value = false }) {
                                        Icon(
                                            imageVector = Icons.Filled.Visibility,
                                            contentDescription = "Hide Password"
                                        )
                                    }
                                } else {
                                    IconButton(onClick = { showPassword.value = true }) {
                                        Icon(
                                            imageVector = Icons.Filled.VisibilityOff,
                                            contentDescription = "Show Password"
                                        )
                                    }
                                }
                            }, visualTransformation = if (showPassword.value) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Done,
                                keyboardType = KeyboardType.Password
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {

                                }
                            ),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = { viewModel.onEvent(LoginEvent.ChangeScreen) }) {
                            Text(text = "Don't have an account? Sign Up")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = {
                            viewModel.onEvent(LoginEvent.LoginButtonClicked)
                        }) {
                            Text(text = "Sign In")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = {
                            viewModel.launchOneTapIntent(context, launcher)
                        }) {
                            Text(text = "Sign In With Google")
                        }
                    }
                }

                LoginPage.NewAccount -> {
                    Column {
                        val showPassword = rememberSaveable { mutableStateOf(false) }
                        OutlinedTextField(
                            value = state.registerFName,
                            onValueChange = { viewModel.onEvent(LoginEvent.RegisterFNameChanged(it)) },
                            label = {
                                Text(text = "First Name")
                            },
                            textStyle = MaterialTheme.typography.bodyMedium,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Next,
                                keyboardType = KeyboardType.Text
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {

                                }
                            ),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = state.registerLName,
                            onValueChange = { viewModel.onEvent(LoginEvent.RegisterLNameChanged(it)) },
                            label = {
                                Text(text = "Last Name")
                            },
                            textStyle = MaterialTheme.typography.bodyMedium,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Next,
                                keyboardType = KeyboardType.Text
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {

                                }
                            ),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = state.registerEmail,
                            onValueChange = { viewModel.onEvent(LoginEvent.RegisterMailChanged(it)) },
                            label = {
                                Text(text = "Email")
                            },
                            textStyle = MaterialTheme.typography.bodyMedium,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Next,
                                keyboardType = KeyboardType.Email
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {

                                }
                            ),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = state.registerPassword,
                            label = {
                                Text(
                                    text = "Password",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            onValueChange = {
                                viewModel.onEvent(LoginEvent.RegisterPasswordChanged(it))
                            },
                            textStyle = MaterialTheme.typography.bodyMedium,
                            trailingIcon = {
                                if (showPassword.value) {
                                    IconButton(onClick = { showPassword.value = false }) {
                                        Icon(
                                            imageVector = Icons.Filled.Visibility,
                                            contentDescription = "Hide Password"
                                        )
                                    }
                                } else {
                                    IconButton(onClick = { showPassword.value = true }) {
                                        Icon(
                                            imageVector = Icons.Filled.VisibilityOff,
                                            contentDescription = "Show Password"
                                        )
                                    }
                                }
                            }, visualTransformation = if (showPassword.value) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Done,
                                keyboardType = KeyboardType.Password
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {

                                }
                            ),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = {
                            viewModel.onEvent(LoginEvent.RegisterButtonClicked)
                        }) {
                            Text(text = "Register")
                        }

                        TextButton(onClick = { viewModel.onEvent(LoginEvent.ChangeScreen) }) {
                            Text(text = "Already have an account? Login")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}
