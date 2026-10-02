package com.example.openfoodfacts.feature.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.openfoodfacts.core.data.MockProductRepository
import com.example.openfoodfacts.core.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel для экрана списка продуктов.
 * Реализует логику пагинации (Вариант А).
 */
class ListViewModel : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _hasMore = MutableStateFlow(true)
    val hasMore: StateFlow<Boolean> = _hasMore.asStateFlow()

    private var currentPage = 1
    private val pageSize = 10

    init {
        loadNextPage()
    }

    /**
     * Загрузка следующей страницы данных.
     */
    fun loadNextPage() {
        if (_isLoading.value || !_hasMore.value) return

        viewModelScope.launch {
            _isLoading.value = true
            val newProducts = MockProductRepository.getProducts(currentPage, pageSize)

            if (newProducts.isEmpty()) {
                _hasMore.value = false
            } else {
                _products.value = _products.value + newProducts
                currentPage++
            }
            _isLoading.value = false
        }
    }
}
