package ru.LezhninLev.openfoodfacts.feature.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ru.LezhninLev.openfoodfacts.core.domain.usecase.GetProductsUseCase
import ru.LezhninLev.openfoodfacts.core.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ListViewModel(
    private val getProductsUseCase: GetProductsUseCase
) : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _hasMore = MutableStateFlow(true)
    val hasMore: StateFlow<Boolean> = _hasMore.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var currentPage = 1
    private val pageSize = 20

    init {
        loadNextPage()
    }

    fun loadNextPage() {
        if (_isLoading.value || !_hasMore.value) return

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                getProductsUseCase(currentPage, pageSize).collect { newProducts ->
                    if (newProducts.isEmpty()) {
                        _hasMore.value = false
                    } else {
                        val currentList = _products.value
                        val existingCodes = currentList.map { it.code }.toSet()
                        val uniqueNewProducts = newProducts.filter { it.code !in existingCodes }

                        if (uniqueNewProducts.isNotEmpty()) {
                            _products.value += uniqueNewProducts
                            if (newProducts.size < pageSize) {
                                _hasMore.value = false
                            } else {
                                currentPage++
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                _error.value = "Ошибка загрузки: ${e.localizedMessage}"
                // hasMore остаётся true, чтобы можно было повторить запрос
            } finally {
                _isLoading.value = false
            }
        }
    }
}