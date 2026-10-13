package ru.LezhninLev.openfoodfacts.feature.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ru.LezhninLev.openfoodfacts.core.domain.usecase.GetProductDetailsUseCase
import ru.LezhninLev.openfoodfacts.core.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailViewModel(
    private val getProductDetailsUseCase: GetProductDetailsUseCase
) : ViewModel() {

    private val _product = MutableStateFlow<Product?>(null)
    val product: StateFlow<Product?> = _product.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadProduct(barcode: String) {
        if (_product.value != null) return

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val result = getProductDetailsUseCase(barcode)
                if (result != null) {
                    _product.value = result
                } else {
                    _error.value = "Product not found"
                }
            } catch (e: Exception) {
                _error.value = "An error occurred: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
