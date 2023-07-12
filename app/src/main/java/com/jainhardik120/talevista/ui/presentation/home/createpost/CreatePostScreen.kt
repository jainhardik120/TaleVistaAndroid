package com.jainhardik120.talevista.ui.presentation.home.createpost

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
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

                }
            }
        }
    })
    Column(Modifier.fillMaxSize()) {
        ExposedDropdownMenuBox(expanded = dropDownExpanded, onExpandedChange = {
            dropDownExpanded = !dropDownExpanded
        }) {
            OutlinedTextField(
                value = if (state.categories.isNotEmpty()) {
                    state.categories[state.selectedCategory].name
                } else {
                    ""
                },
                onValueChange = {},
                modifier = Modifier
                    .menuAnchor(),
                label = { Text(text = "Semester") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropDownExpanded)
                },
                readOnly = true
            )
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
        Box(Modifier.weight(1f)) {

            BasicTextField(
                value = state.postContent,
                onValueChange = { viewModel.onEvent(CreatePostsEvent.PostContentChanged(it)) },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                cursorBrush = SolidColor(LocalContentColor.current),
                textStyle = LocalTextStyle.current.copy(color = LocalContentColor.current)
            )
        }
        Column(Modifier.imePadding()) {
            Row(
                modifier = Modifier
                    .height(72.dp)
                    .wrapContentHeight()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val sendMessageEnabled = state.postContent.isNotBlank()
                val border = if (!sendMessageEnabled) {
                    BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                } else {
                    null
                }
                Spacer(modifier = Modifier.weight(1f))
                val disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                val buttonColors = ButtonDefaults.buttonColors(
                    disabledContainerColor = Color.Transparent,
                    disabledContentColor = disabledContentColor
                )
                Button(
                    modifier = Modifier.height(36.dp),
                    enabled = sendMessageEnabled,
                    onClick = {
                        viewModel.onEvent(CreatePostsEvent.SendButtonClicked)
                    },
                    colors = buttonColors,
                    border = border,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        "Send",
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}
