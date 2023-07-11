package com.jainhardik120.talevista.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.jainhardik120.talevista.ui.presentation.home.posts.ListState

@Composable
fun PaginatingColumn(
    modifier: Modifier = Modifier,
    lazyListState: LazyListState,
    listState: ListState,
    items: LazyListScope.() -> Unit,
    loadingItem: @Composable (() -> Unit)? = null
) {
    Box(modifier = modifier) {
        if (listState == ListState.LOADING && loadingItem != null) {
            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
                loadingItem()
            }
        }
        LazyColumn(Modifier.fillMaxSize(), state = lazyListState, content = {
            items()
            item {
                if (listState == ListState.PAGINATING && loadingItem != null) {
                    loadingItem()
                }
            }
        })
    }

}