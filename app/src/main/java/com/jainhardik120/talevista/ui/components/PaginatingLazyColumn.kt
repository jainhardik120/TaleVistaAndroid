package com.jainhardik120.talevista.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.jainhardik120.talevista.ui.presentation.home.posts.ListState

@Composable
fun <T> PaginatingColumn(
    modifier: Modifier = Modifier,
    lazyListState: LazyListState,
    data: List<T> = emptyList(),
    listState: ListState,
    item: @Composable (T, Int) -> Unit
) {
    LazyColumn(modifier = modifier, state = lazyListState, content = {
        itemsIndexed(data) { index, item ->
            item(item, index)
        }
        item {
            when (listState) {
                ListState.LOADING -> {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator()
                    }
                }

                ListState.PAGINATING -> {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        CircularProgressIndicator()
                    }
                }

                ListState.PAGINATION_EXHAUST -> {
                    Text(text = "Paginating Exhaust")
                }

                else -> {

                }
            }
        }
    })
}