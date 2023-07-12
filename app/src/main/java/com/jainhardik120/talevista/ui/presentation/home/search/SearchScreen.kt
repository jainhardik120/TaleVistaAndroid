package com.jainhardik120.talevista.ui.presentation.home.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.jainhardik120.talevista.ui.presentation.home.HomeScreenRoutes
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    navController: NavHostController
) {
    val searchText by viewModel.searchText.collectAsState()
    val users by viewModel.users.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val scope = rememberCoroutineScope()
    Column(
        Modifier
            .statusBarsPadding()
            .imePadding()
    ) {
        Scaffold(
            topBar = {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(10.dp), verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        scope.launch {
                            navController.navigateUp()
                        }
                    }) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back Arrow")
                    }
                    Box(
                        Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        val keyboardController = LocalSoftwareKeyboardController.current
                        BasicTextField(
                            value = searchText,
                            onValueChange = viewModel::onSearchChanged,
                            Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp)
                                .align(Alignment.CenterStart),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Search
                            ),
                            keyboardActions = KeyboardActions(
                                onSearch = {
                                    keyboardController?.hide()
                                }
                            ),
                            maxLines = 1,
                            singleLine = true
                        )
                        if (searchText.isEmpty()) {
                            Text(
                                text = "Search",
                                Modifier
                                    .padding(start = 16.dp)
                                    .align(Alignment.CenterStart),
                                style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                    }
                }

            }
        ) {
            if (isSearching) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(it),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                ) {
                    itemsIndexed(users) { index, item ->
                        Row(
                            Modifier
                                .clickable(
                                    role = Role.Button,
                                    onClick = {
                                        navController.navigate(
                                            HomeScreenRoutes.ProfileScreen.withArgs(
                                                item._id
                                            )
                                        )
                                    }
                                )
                                .padding(10.dp)
                                .fillMaxWidth()) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .padding(4.dp)
                                    .clip(RoundedCornerShape(100))
                            ) {
                                AsyncImage(model = item.picture, contentDescription = "User Icon")
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(
                                Modifier
                                    .height(60.dp)
                                    .weight(1f)
                                    .fillMaxWidth(), verticalArrangement = Arrangement.Center
                            ) {
                                Text(text = item.username, fontWeight = FontWeight.ExtraBold)
                                if (item.first_name != null || item.last_name != null) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(text = "${item.first_name} ${item.last_name}")
                                }
                            }
                        }
                        if (index != users.size - 1) {
                            Divider()
                        }
                    }
                }
            }

        }
    }
}


