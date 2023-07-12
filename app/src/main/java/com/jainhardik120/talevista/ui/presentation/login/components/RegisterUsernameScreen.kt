package com.jainhardik120.talevista.ui.presentation.login.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.jainhardik120.talevista.ui.presentation.login.Gender
import com.jainhardik120.talevista.ui.presentation.login.LoginEvent
import com.jainhardik120.talevista.ui.presentation.login.LoginState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun RegisterUsernameScreen(
    onEvent: (LoginEvent) -> Unit,
    state: LoginState,
    username: String,
    usernameAvailable: Boolean
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var genderMenuExpanded by remember { mutableStateOf(false) }
    var datePickerExpanded by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val scope = rememberCoroutineScope()

    val dobFocusRequester = remember {
        FocusRequester()
    }
    val usernameFocusRequester = remember {
        FocusRequester()
    }

    val genderEntries = remember {
        listOf(
            Gender.MALE,
            Gender.FEMALE,
            Gender.OTHER,
            Gender.NA
        )
    }
    val dateString = remember(state.dob) {
        derivedStateOf {
            val date = Date(state.dob)
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.timeZone = TimeZone.getDefault()

            sdf.format(date)
        }
    }
    if (datePickerExpanded) {
        val confirmEnabled = remember(datePickerState.selectedDateMillis) {
            derivedStateOf {
                val selectedDateMillis = datePickerState.selectedDateMillis
                val currentDate = Calendar.getInstance().timeInMillis

                if (selectedDateMillis != null) {
                    val selectedDate = Date(selectedDateMillis)
                    val ageInMillis = currentDate - selectedDate.time
                    val ageInYears = TimeUnit.MILLISECONDS.toDays(ageInMillis) / 365

                    ageInYears >= 18
                } else {
                    false
                }
            }
        }

        DatePickerDialog(onDismissRequest = {
            datePickerExpanded = false
        }, confirmButton = {
            TextButton(
                onClick = {
                    datePickerExpanded = false
                    onEvent(
                        LoginEvent.DateOfBirthChanged(
                            datePickerState.selectedDateMillis ?: 0
                        )
                    )
                },
                enabled = confirmEnabled.value
            ) {
                Text("OK")
            }
        },
            dismissButton = {
                TextButton(
                    onClick = {
                        datePickerExpanded = false
                    }
                ) {
                    Text("Cancel")
                }
            }) {
            DatePicker(state = datePickerState, title = {
                Text(
                    modifier = Modifier.padding(
                        PaddingValues(start = 24.dp, end = 12.dp, top = 16.dp)
                    ), text = "Select your date of birth"
                )
            })
        }
    }
    Column(
        Modifier
            .padding(horizontal = 8.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = state.firstName,
            onValueChange = {
                onEvent(LoginEvent.RegisterFNameChanged(it))
            },
            label = {
                Text(text = "First Name")
            },
            textStyle = MaterialTheme.typography.bodyMedium,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Text
            ),
            keyboardActions = KeyboardActions(
                onNext = {

                }
            ),
            singleLine = true
        )
        OutlinedTextField(
            value = state.lastName,
            onValueChange = { onEvent(LoginEvent.RegisterLNameChanged(it)) },
            label = {
                Text(text = "Last Name")
            },
            textStyle = MaterialTheme.typography.bodyMedium,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Text
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    keyboardController?.hide()
                    scope.launch {
                        delay(500)
                        genderMenuExpanded = true
                    }
                }
            ),
            singleLine = true
        )

        ExposedDropdownMenuBox(
            expanded = genderMenuExpanded,
            onExpandedChange = {
                genderMenuExpanded = !genderMenuExpanded
            }
        ) {

            OutlinedTextField(
                value = state.gender.displayName,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.menuAnchor(),
                label = {
                    Text(
                        text = "Gender"
                    )
                },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderMenuExpanded) })

            ExposedDropdownMenu(
                expanded = genderMenuExpanded,
                onDismissRequest = { genderMenuExpanded = false }) {
                genderEntries.forEach {
                    DropdownMenuItem(text = { Text(it.displayName) }, onClick = {
                        genderMenuExpanded = false
                        onEvent(LoginEvent.GenderChanged(it))
                        if (state.dob == 0L) {
                            scope.launch {
                                delay(200)
                                dobFocusRequester.requestFocus()
                            }
                        }
                    })
                }
            }
        }
        OutlinedTextField(
            value = dateString.value,
            onValueChange = { },
            label = {
                Text(text = "Date Of Birth")
            },
            textStyle = MaterialTheme.typography.bodyMedium,
            trailingIcon = {
                IconButton(onClick = {
                    datePickerExpanded = !datePickerExpanded
                }) {
                    Icon(Icons.Rounded.CalendarMonth, contentDescription = "Calendar Icon")
                }
            },
            modifier = Modifier
                .focusRequester(dobFocusRequester)
                .onFocusEvent {
                    if (it.isFocused) {
                        datePickerExpanded = true
                    }
                },
            readOnly = true,
            singleLine = true
        )
        OutlinedTextField(
            value = username,
            onValueChange = { onEvent(LoginEvent.RegisterUserNameChanged(it)) },
            label = {
                Text(text = "Username")
            },
            textStyle = MaterialTheme.typography.bodyMedium,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Text
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    onEvent(LoginEvent.RegisterUsernameButtonClicked)
                }
            ),
            modifier = Modifier
                .focusRequester(usernameFocusRequester)
                .onFocusChanged {
                    if (it.isFocused) {
                        keyboardController?.show()
                    }
                },
            singleLine = true,
            isError = (username.isNotEmpty() && !usernameAvailable)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { onEvent(LoginEvent.RegisterUsernameButtonClicked) }) {
            Text(text = "Create Account")
        }
    }
}