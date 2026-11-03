package ru.LezhninLev.openfoodfacts.feature.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.LezhninLev.openfoodfacts.core.error.createCoroutineExceptionHandler
import ru.LezhninLev.openfoodfacts.core.domain.usecase.GetFavoriteByCodeUseCase
import ru.LezhninLev.openfoodfacts.core.domain.usecase.GetProductDetailsUseCase
import ru.LezhninLev.openfoodfacts.core.domain.usecase.IsFavoriteUseCase
import ru.LezhninLev.openfoodfacts.core.domain.usecase.ToggleFavoriteUseCase

class DetailViewModel(
    private val getProductDetailsUseCase: GetProductDetailsUseCase,
    private val isFavoriteUseCase: IsFavoriteUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val getFavoriteByCodeUseCase: GetFavoriteByCodeUseCase
) : ViewModel() {
    private val _product = MutableStateFlow<ProductDetailUiModel?>(null)
    val product: StateFlow<ProductDetailUiModel?> = _product.asStateFlow()

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
            viewModelScope.launch(createCoroutineExceptionHandler { exception ->
                _error.value = "Ошибка: ${exception.localizedMessage}"
            }) {
                isFavoriteUseCase(barcode).collect { _isFavorite.value = it }
            }
        }
        if (_product.value != null) return

        viewModelScope.launch(createCoroutineExceptionHandler { exception ->
            _error.value = "Ошибка: ${exception.localizedMessage}"
        }) {
            _isLoading.value = true
            _error.value = null
            try {
                val fromNetwork = getProductDetailsUseCase(barcode)
                val result = fromNetwork ?: getFavoriteByCodeUseCase(barcode)
                if (result != null) _product.value = result.toUiModel()
                else _error.value = "Продукт не найден в каталоге и избранном"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleFavorite() {
        val current = _product.value?.product ?: return
        viewModelScope.launch(createCoroutineExceptionHandler { exception ->
            _error.value = "Ошибка: ${exception.localizedMessage}"
        }) {
            toggleFavoriteUseCase(current)
        }
    }

    private fun ru.LezhninLev.openfoodfacts.core.model.Product.toUiModel() =
        ProductDetailUiModel(
            product = this,
            barcodeLabel = "Штрихкод: $code",
            nutriScore = nutriscoreGrade?.let { grade ->
                NutriScoreUiModel(
                    label = "Nutri-Score $grade",
                    colorArgb = when (grade.uppercase()) {
                        "A" -> 0xFF008B4C
                        "B" -> 0xFFF9C80E
                        "C" -> 0xFFFFA500
                        "D" -> 0xFFE45824
                        "E" -> 0xFFED1C24
                        else -> 0xFF808080
                    }
                )
            }
        )
}
