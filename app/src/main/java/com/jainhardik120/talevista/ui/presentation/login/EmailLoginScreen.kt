package com.jainhardik120.talevista.ui.presentation.login

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun EmailLoginScreen(
    onEvent: (LoginEvent) -> Unit,
    state: LoginState,
    handleIntentResult: (ActivityResult) -> Unit
) {
    val showPassword = rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = handleIntentResult
    )
    Column(
        Modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth()
    ) {
        OutlinedTextField(
            value = state.loginEmail,
            onValueChange = { onEvent(LoginEvent.LoginMailChanged(it)) },
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
                onEvent(LoginEvent.LoginPasswordChanged(it))
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
        TextButton(onClick = { onEvent(LoginEvent.ChangeScreen) }) {
            Text(text = "Don't have an account? Sign Up")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            onEvent(LoginEvent.LoginButtonClicked)
        }) {
            Text(text = "Sign In")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            onEvent(LoginEvent.LaunchOneTapClient(context, launcher))
        }) {
            Text(text = "Sign In With Google")
        }
    }
}