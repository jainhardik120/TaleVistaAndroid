package com.jainhardik120.talevista.ui.presentation.login.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.jainhardik120.talevista.R
import com.jainhardik120.talevista.ui.presentation.login.LoginEvent
import com.jainhardik120.talevista.ui.presentation.login.LoginState

@OptIn(ExperimentalComposeUiApi::class)
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


    val keyboardController = LocalSoftwareKeyboardController.current
    Column(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth()
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                value = state.loginEmail,
                onValueChange = { onEvent(LoginEvent.LoginMailChanged(it)) },
                label = {
                    Text(text = "Email or username")
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
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
                        onEvent(LoginEvent.LoginButtonClicked)
                    }
                ),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Text(
                    text = "Forgot Password?",
                    modifier = Modifier
                        .clip(RoundedCornerShape(2.dp))
                        .clickable {
                            onEvent(LoginEvent.ForgotPasswordClicked)
                        }
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                onClick = {
                    onEvent(LoginEvent.LoginButtonClicked)
                }) {
                Text(text = "Sign In")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(
                    Modifier
                        .weight(1F)
                        .fillMaxWidth()
                )
                Text(text = "OR", modifier = Modifier.padding(horizontal = 4.dp))
                Divider(
                    Modifier
                        .weight(1F)
                        .fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
                onClick = { onEvent(LoginEvent.LaunchOneTapClient(context, launcher)) }) {
                Text(text = "Sign In With Google")
            }
        }

        Row(
            Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clickable(enabled = true, role = Role.Button) {
                    onEvent(LoginEvent.SignUpTextClicked)
                },
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                buildAnnotatedString {
                    append("New to ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(stringResource(id = R.string.app_name))
                    }
                    append("? ")
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        append("Register")
                    }
                }
            )
        }
    }
}