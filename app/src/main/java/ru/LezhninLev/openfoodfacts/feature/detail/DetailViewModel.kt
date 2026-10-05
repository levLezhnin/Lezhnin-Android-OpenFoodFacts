package ru.LezhninLev.openfoodfacts.feature.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ru.LezhninLev.openfoodfacts.core.data.MockProductRepository
import ru.LezhninLev.openfoodfacts.core.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel для экрана деталей продукта.
 */
class DetailViewModel : ViewModel() {

    private val _product = MutableStateFlow<Product?>(null)
    val product: StateFlow<Product?> = _product.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /**
     * Загрузка деталей продукта по его коду.
     */
    fun loadProduct(barcode: String) {
        if (_product.value != null) return // Уже загружено

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = MockProductRepository.getProductDetails(barcode)
            if (result != null) {
                _product.value = result
            } else {
                _error.value = "Product not found"
            }
            _isLoading.value = false
        }
    }
}
