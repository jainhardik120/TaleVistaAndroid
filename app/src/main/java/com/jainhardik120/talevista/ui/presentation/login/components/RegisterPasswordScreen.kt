package com.jainhardik120.talevista.ui.presentation.login.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.jainhardik120.talevista.ui.presentation.login.LoginEvent
import com.jainhardik120.talevista.ui.presentation.login.LoginState

@Composable
fun RegisterPasswordScreen(
    onEvent: (LoginEvent) -> Unit,
    state: LoginState
) {
    val showPassword = rememberSaveable { mutableStateOf(false) }
    Column(
        Modifier
            .padding(horizontal = 8.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            value = state.registerPassword,
            label = {
                Text(
                    text = "Password",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            onValueChange = {
                onEvent(LoginEvent.RegisterPasswordChanged(it))
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
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Password
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    if (state.registerPasswordButtonEnabled) {
                        onEvent(LoginEvent.RegisterPasswordButtonClicked)

                    }
                }
            ),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
            onClick = { onEvent(LoginEvent.RegisterPasswordButtonClicked) },
            enabled = state.registerPasswordButtonEnabled
        ) {
            Text(text = "Next")
        }
    }
}