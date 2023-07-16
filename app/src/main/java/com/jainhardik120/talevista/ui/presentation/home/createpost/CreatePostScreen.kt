package com.jainhardik120.talevista.ui.presentation.home.createpost

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jainhardik120.talevista.util.NAVIGATE_UP_ROUTE
import com.jainhardik120.talevista.util.UiEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    viewModel: CreatePostViewModel,
    navController: NavController
) {
    val hostState = remember { SnackbarHostState() }
    var dropDownExpanded by remember {
        mutableStateOf(false)
    }
    val state = viewModel.state
    LaunchedEffect(key1 = true, block = {
        viewModel.uiEvent.collect {
            when (it) {
                is UiEvent.Navigate -> {
                    if (it.route == NAVIGATE_UP_ROUTE) {
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


    if (viewModel.state.isShowingDialog) {
        AlertDialog(
            onDismissRequest = {
                viewModel.onEvent(CreatePostsEvent.DialogDismissed)
            },
            confirmButton = {
                TextButton(onClick = { viewModel.onEvent(CreatePostsEvent.DialogConfirmButtonClicked) }) {
                    Text(text = "Yes")
                }
            },
            title = {
                Text(text = "Unsaved Changes")
            },
            text = {
                Text(text = "You have unsaved changes. Going back would discard these changes. Are you sure you want to go back?")
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onEvent(CreatePostsEvent.DialogDismissed) }) {
                    Text(text = "Cancel")
                }
            },
        )
    }


    Column(
        Modifier
            .statusBarsPadding()
            .imePadding()
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = hostState) },
            topBar = {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(10.dp), verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        viewModel.onEvent(CreatePostsEvent.CancelButtonClicked)
                    }) {
                        Icon(Icons.Rounded.Close, contentDescription = "Cancel Icon")
                    }
                    ExposedDropdownMenuBox(
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .weight(1f)
                            .fillMaxWidth(),
                        expanded = dropDownExpanded,
                        onExpandedChange = {
                            dropDownExpanded = !dropDownExpanded
                        }) {
                        Surface(
                            Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .clickable {
                                    dropDownExpanded = true
                                },
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = contentColorFor(MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Row(
                                Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (state.categories.isNotEmpty()) {
                                        state.categories[state.selectedCategory].name
                                    } else {
                                        ""
                                    }
                                )
                                Spacer(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth()
                                )
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropDownExpanded)
                            }
                        }
                        if (state.categories.isNotEmpty()) {
                            ExposedDropdownMenu(
                                expanded = dropDownExpanded,
                                onDismissRequest = { dropDownExpanded = false }) {
                                state.categories.forEachIndexed { index, categoriesItem ->
                                    DropdownMenuItem(
                                        text = { Text(text = categoriesItem.name) },
                                        onClick = {
                                            viewModel.onEvent(CreatePostsEvent.CategoryChanged(index))
                                            dropDownExpanded = false
                                        },
                                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    IconButton(
                        onClick = { viewModel.onEvent(CreatePostsEvent.SendButtonClicked) },
                        enabled = state.postContent.isNotBlank()
                    ) {
                        Icon(Icons.Rounded.Send, contentDescription = "Send Icon")
                    }
                }

            }
        ) { paddingValues ->
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                OutlinedTextField(value = state.postContent, onValueChange = {
                    viewModel.onEvent(CreatePostsEvent.PostContentChanged(it))
                }, modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp))
            }
        }
    }
}
