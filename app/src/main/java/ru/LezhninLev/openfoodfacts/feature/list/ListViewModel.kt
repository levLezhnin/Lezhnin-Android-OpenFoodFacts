package ru.LezhninLev.openfoodfacts.feature.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.LezhninLev.openfoodfacts.core.cache.FilterBadgeCache
import ru.LezhninLev.openfoodfacts.core.domain.usecase.ObserveFiltersUseCase
import ru.LezhninLev.openfoodfacts.core.domain.usecase.GetProductsUseCase
import ru.LezhninLev.openfoodfacts.core.error.createCoroutineExceptionHandler
import ru.LezhninLev.openfoodfacts.core.model.Product
import ru.LezhninLev.openfoodfacts.core.model.SearchFilters

class ListViewModel(
    private val getProductsUseCase: GetProductsUseCase,
    private val observeFiltersUseCase: ObserveFiltersUseCase,
    filterBadgeCache: FilterBadgeCache
) : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _hasMore = MutableStateFlow(true)
    val hasMore: StateFlow<Boolean> = _hasMore.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _showFilterBadge = MutableStateFlow(false)
    val showFilterBadge: StateFlow<Boolean> = _showFilterBadge.asStateFlow()

    private var currentPage = 1
    private val pageSize = 20

    private var currentFilters = SearchFilters()
    private var loadJob: Job? = null
    private var loadGeneration = 0

    init {
        viewModelScope.launch(createCoroutineExceptionHandler { exception ->
            _error.value = "Ошибка фильтров: ${exception.localizedMessage}"
        }) {
            observeFiltersUseCase().collect { filters ->
                currentFilters = filters
                reload()
            }
        }
        viewModelScope.launch(createCoroutineExceptionHandler { exception ->
            _error.value = "Ошибка фильтров: ${exception.localizedMessage}"
        }) {
            filterBadgeCache.show.collect { show -> _showFilterBadge.value = show }
        }
    }

    private fun reload() {
        loadJob?.cancel()
        loadGeneration++
        _isLoading.value = false
        _products.value = emptyList()
        _hasMore.value = true
        _error.value = null
        currentPage = 1
        loadNextPage()
    }

    fun loadNextPage() {
        if (_isLoading.value || !_hasMore.value) return
        val gen = ++loadGeneration
        loadJob = viewModelScope.launch(
            createCoroutineExceptionHandler { exception ->
                if (gen == loadGeneration) {
                    _error.value = "Ошибка загрузки: ${exception.localizedMessage}"
                }
            }
        ) {
            _isLoading.value = true
            _error.value = null
            try {
                getProductsUseCase(currentPage, pageSize, currentFilters).collect { newProducts ->
                    if (gen != loadGeneration) return@collect
                    if (newProducts.isEmpty()) {
                        _hasMore.value = false
                    } else {
                        val existingCodes = _products.value.map { it.code }.toSet()
                        val unique = newProducts.filter { it.code !in existingCodes }
                        if (unique.isNotEmpty()) {
                            _products.value += unique
                            if (newProducts.size < pageSize) {
                                _hasMore.value = false
                            } else {
                                currentPage++
                            }
                        } else {
                            _hasMore.value = false
                        }
                    }
                }
            } finally {
                if (gen == loadGeneration) _isLoading.value = false
            }
        }
    }
}
