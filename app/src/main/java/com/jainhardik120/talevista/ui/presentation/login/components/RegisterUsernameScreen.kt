package com.jainhardik120.talevista.ui.presentation.login.components

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.jainhardik120.talevista.ui.presentation.login.Gender
import com.jainhardik120.talevista.ui.presentation.login.LoginEvent
import com.jainhardik120.talevista.ui.presentation.login.LoginState
import com.jainhardik120.talevista.util.BASE_SERVER_URL
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterUsernamePreview() {
    RegisterUsernameScreen(
        onEvent = {},
        state = LoginState(),
        username = "_.hardikj",
        usernameAvailable = true
    )
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun RegisterUsernameScreen(
    onEvent: (LoginEvent) -> Unit,
    state: LoginState,
    username: String,
    usernameAvailable: Boolean
) {
    var genderMenuExpanded by remember { mutableStateOf(false) }
    var datePickerExpanded by remember { mutableStateOf(false) }
    var bottomSheetExpanded by remember { mutableStateOf(false) }
    var privacyPolicyShowing by remember { mutableStateOf(false) }

    if (privacyPolicyShowing) {
        Dialog(
            onDismissRequest = { privacyPolicyShowing = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(Modifier.fillMaxSize()) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(onClick = { privacyPolicyShowing = false }) {
                            Icon(Icons.Filled.Close, contentDescription = "Close Icon")
                        }
                        Text(
                            text = "Privacy Policy",
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.width(40.dp))
                    }
                    AndroidView(factory = {
                        WebView(it).apply {
                            webViewClient = WebViewClient()
                            loadUrl("$BASE_SERVER_URL/privacy_policy.html")
                        }
                    },
                        Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .fillMaxHeight())
                }
            }
        }
    }

    val images by remember {
        mutableStateOf(
            listOf(
                "avatar1).png",
                "avatar2).png",
                "avatar3).png",
                "avatar4).png",
                "avatar5).png",
                "avatar6).png",
                "avatar7).png",
                "avatar8).png",
                "avatar9).png",
                "avatar10).png",
                "avatar11).png",
                "avatar12).png",
                "avatar13).png",
                "avatar14).png",
                "avatar15).png",
                "avatar16).png",
                "avatar17).png",
                "avatar18).png",
                "avatar19).png",
                "avatar20).png",
                "avatar21).png",
                "avatar22).png",
                "avatar23).png",
                "avatar24).png",
                "avatar25).png",
                "avatar26).png",
                "avatar27).png",
                "avatar28).png",
                "avatar29).png",
                "avatar30).png"
            ).map {
                "$BASE_SERVER_URL/$it"
            }
        )
    }

    if (bottomSheetExpanded) {
        ModalBottomSheet(onDismissRequest = { bottomSheetExpanded = false }, dragHandle = null) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(6),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                content = {
                    itemsIndexed(images) { _, item ->
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = item,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp)
                                    .aspectRatio(1f, matchHeightConstraintsFirst = true)
                                    .clip(
                                        CircleShape
                                    )
                                    .clickable {
                                        bottomSheetExpanded = false
                                        onEvent(LoginEvent.ImageUrlChanged(item))
                                    },
                                contentDescription = "Profile Icon"
                            )
                        }
                    }
                })
        }
    }
    val datePickerState = rememberDatePickerState()
    val scope = rememberCoroutineScope()

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
            .padding(horizontal = 24.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            AsyncImage(
                model = state.picture,
                modifier = Modifier
                    .size(100.dp)
                    .weight(3f)
                    .aspectRatio(1f, matchHeightConstraintsFirst = true)
                    .border(width = 1.dp, color = Color.LightGray, shape = CircleShape)
                    .padding(3.dp)
                    .clip(
                        CircleShape
                    )
                    .clickable {
                        bottomSheetExpanded = true
                    },
                contentDescription = "Profile Icon"
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = state.name,
            onValueChange = { onEvent(LoginEvent.RegisterNameChanged(it)) },
            label = {
                Text(text = "Name")
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
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth()) {
            ExposedDropdownMenuBox(
                expanded = genderMenuExpanded,
                onExpandedChange = {
                    genderMenuExpanded = !genderMenuExpanded
                },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {

                OutlinedTextField(
                    value = state.gender?.displayName ?: "Select",
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
                        })
                    }
                }
            }
            Spacer(Modifier.width(16.dp))
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
                    .onFocusEvent {
                        if (it.isFocused) {
                            datePickerExpanded = true
                        }
                    }
                    .weight(1f)
                    .fillMaxWidth(),
                readOnly = true,
                singleLine = true
            )
        }


        Spacer(modifier = Modifier.height(8.dp))
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
                .fillMaxWidth(),
            singleLine = true,
            isError = (username.isNotEmpty() && !usernameAvailable)
        )
        Spacer(modifier = Modifier.height(8.dp))
        var checkBoxChecked by remember {
            mutableStateOf(false)
        }
        Row(
            Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Checkbox(
                checked = checkBoxChecked,
                onCheckedChange = { checkBoxChecked = it },
                Modifier.padding(end = 16.dp)
            )
            Text(
                buildAnnotatedString {
                    append("I agree and accept ")
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        append("Privacy Policy")
                    }
                },
                modifier = Modifier.clickable {
                    privacyPolicyShowing = true
                }
            )
        }
        Spacer(modifier = Modifier.height(8.dp))


        Button(
            onClick = { onEvent(LoginEvent.RegisterUsernameButtonClicked) },
            Modifier.fillMaxWidth(),
            enabled = checkBoxChecked && (state.name.isNotEmpty() && username.isNotEmpty() && state.gender != null)
        ) {
            Text(text = "Create Account")
        }
    }
}