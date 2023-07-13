package com.jainhardik120.talevista.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.jainhardik120.talevista.R
import com.jainhardik120.talevista.domain.repository.AuthController
import com.jainhardik120.talevista.ui.theme.TaleVistaTheme
import com.jainhardik120.talevista.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PasswordResetActivity : ComponentActivity() {

    @Inject
    lateinit var authController: AuthController

    fun openMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intentData: Uri? = intent.data
        val token = intentData?.getQueryParameter("token")
        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (token == null) {
            finish()
        }

        setContentView(
            ComposeView(this).apply {
                consumeWindowInsets = false
                setContent {
                    TaleVistaTheme {
                        Surface(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Column(
                                Modifier.imePadding()
                            ) {
                                Scaffold(topBar = {
                                    CenterAlignedTopAppBar(title = {
                                        Text(text = stringResource(id = R.string.app_name))
                                    })
                                }) { paddingValues ->
                                    val context = LocalContext.current
                                    Column(
                                        Modifier
                                            .fillMaxSize()
                                            .padding(paddingValues)
                                            .padding(horizontal = 8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {

                                        val scope = rememberCoroutineScope()
                                        var newPassword by remember { mutableStateOf("") }

                                        fun resetPassword() {
                                            scope.launch {
                                                when (val response = authController.resetPassword(
                                                    token ?: "",
                                                    newPassword
                                                )) {
                                                    is Resource.Error -> {
                                                        Toast.makeText(
                                                            context,
                                                            response.message,
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }

                                                    is Resource.Success -> {
                                                        Toast.makeText(
                                                            context,
                                                            response.data?.message,
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                        openMainActivity()
                                                    }
                                                }
                                            }
                                        }

                                        val showPassword =
                                            rememberSaveable { mutableStateOf(false) }

                                        Spacer(modifier = Modifier.height(16.dp))
                                        OutlinedTextField(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp),
                                            value = newPassword,
                                            label = {
                                                Text(
                                                    text = "Password",
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                            },
                                            onValueChange = {
                                                newPassword = it
                                            },
                                            textStyle = MaterialTheme.typography.bodyMedium,
                                            trailingIcon = {
                                                if (showPassword.value) {
                                                    IconButton(onClick = {
                                                        showPassword.value = false
                                                    }) {
                                                        Icon(
                                                            imageVector = Icons.Filled.Visibility,
                                                            contentDescription = "Hide Password"
                                                        )
                                                    }
                                                } else {
                                                    IconButton(onClick = {
                                                        showPassword.value = true
                                                    }) {
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
                                                    resetPassword()
                                                }
                                            ),
                                            singleLine = true
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Button(modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp),
                                            onClick = {
                                                resetPassword()
                                            }) {
                                            Text(text = "Update Password")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        )
    }
}

