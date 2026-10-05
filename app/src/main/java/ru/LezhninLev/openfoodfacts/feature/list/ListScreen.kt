package ru.LezhninLev.openfoodfacts.feature.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Основной экран списка продуктов.
 */
@Composable
fun ListScreen(
    onProductClick: (String) -> Unit,
    viewModel: ListViewModel = viewModel()
) {
    val products by viewModel.products.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val hasMore by viewModel.hasMore.collectAsState()

    val listState = rememberLazyListState()

    // Простейшая реализация пагинации при скролле к концу списка
    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem != null && lastVisibleItem.index >= listState.layoutInfo.totalItemsCount - 2
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value && hasMore && !isLoading) {
            viewModel.loadNextPage()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(products, key = { it.code }) { product ->
                ProductListItem(
                    product = product,
                    onClick = { onProductClick(product.code) }
                )
            }

            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}
