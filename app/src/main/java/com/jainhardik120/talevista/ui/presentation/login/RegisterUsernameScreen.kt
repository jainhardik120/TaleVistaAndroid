package com.jainhardik120.talevista.ui.presentation.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun RegisterUsernameScreen(
    onEvent: (LoginEvent) -> Unit,
    state: LoginState,
    isGoogle: Boolean
) {
    Column(
        Modifier
            .padding(horizontal = 8.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = state.registerUsername,
            onValueChange = { onEvent(LoginEvent.RegisterUserNameChanged(it)) },
            label = {
                Text(text = "Username")
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
        if (state.showUsernames) {

            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(content = {
                itemsIndexed(state.recommendedUserNames) { _, item ->
                    Text(text = item)
                }
            })
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { onEvent(LoginEvent.RegisterUsernameButtonClicked(isGoogle)) }) {
            Text(text = "Create Account")
        }
    }
}