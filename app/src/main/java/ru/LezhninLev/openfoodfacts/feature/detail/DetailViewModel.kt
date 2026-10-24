package ru.LezhninLev.openfoodfacts.feature.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.LezhninLev.openfoodfacts.core.domain.usecase.GetFavoriteByCodeUseCase
import ru.LezhninLev.openfoodfacts.core.domain.usecase.GetProductDetailsUseCase
import ru.LezhninLev.openfoodfacts.core.domain.usecase.IsFavoriteUseCase
import ru.LezhninLev.openfoodfacts.core.domain.usecase.ToggleFavoriteUseCase
import ru.LezhninLev.openfoodfacts.core.model.Product

class DetailViewModel(
    private val getProductDetailsUseCase: GetProductDetailsUseCase,
    private val isFavoriteUseCase: IsFavoriteUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val getFavoriteByCodeUseCase: GetFavoriteByCodeUseCase
) : ViewModel() {
    private val _product = MutableStateFlow<Product?>(null)
    val product: StateFlow<Product?> = _product.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    private var favoriteJobStarted = false

    fun loadProduct(barcode: String) {
        if (!favoriteJobStarted) {
            favoriteJobStarted = true
            viewModelScope.launch {
                isFavoriteUseCase(barcode).collect { _isFavorite.value = it }
            }
        }
        if (_product.value != null) return

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val fromNetwork = getProductDetailsUseCase(barcode)
                val result = fromNetwork ?: getFavoriteByCodeUseCase(barcode)
                if (result != null) _product.value = result
                else _error.value = "Продукт недоступен: нет сети и нет в избранном"
            } catch (e: Exception) {
                _error.value = "Ошибка: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleFavorite() {
        val current = _product.value ?: return
        viewModelScope.launch { toggleFavoriteUseCase(current) }
    }
}