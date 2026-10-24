package ru.LezhninLev.openfoodfacts.feature.filter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.LezhninLev.openfoodfacts.core.data.datastore.FilterPreferences
import ru.LezhninLev.openfoodfacts.core.domain.usecase.GetCategoriesUseCase
import ru.LezhninLev.openfoodfacts.core.model.SearchFilters

class FilterViewModel(
    private val filterPreferences: FilterPreferences,
    private val getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {

    data class UiState(
        val category: String = "",
        val brand: String = "",
        val grade: String = "",
        val availableCategories: List<String> = emptyList(),
        val categoriesLoading: Boolean = false,
        val categoriesError: String? = null
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val saved = filterPreferences.filtersFlow.first()
            _state.update {
                it.copy(
                    category = saved.category.orEmpty(),
                    brand = saved.brand.orEmpty(),
                    grade = saved.grade.orEmpty()
                )
            }
        }
        loadCategories()
    }

    fun onCategoryChange(value: String) = _state.update { it.copy(category = value) }
    fun onBrandChange(value: String) = _state.update { it.copy(brand = value) }
    fun onGradeChange(value: String) = _state.update { it.copy(grade = value) }

    fun apply(onDone: () -> Unit) {
        viewModelScope.launch {
            val s = _state.value
            filterPreferences.save(
                SearchFilters(
                    category = s.category.ifBlank { null },
                    brand = s.brand.ifBlank { null },
                    grade = s.grade.ifBlank { null }
                )
            )
            onDone()
        }
    }

    fun reset(onDone: () -> Unit) {
        viewModelScope.launch {
            filterPreferences.save(SearchFilters())
            onDone()
        }
    }

    fun retryCategories() = loadCategories()

    private fun loadCategories() {
        viewModelScope.launch {
            _state.update { it.copy(categoriesLoading = true, categoriesError = null) }
            try {
                val cats = getCategoriesUseCase()
                _state.update { it.copy(availableCategories = cats, categoriesLoading = false) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        categoriesError = "Не удалось загрузить категории: ${e.localizedMessage}",
                        categoriesLoading = false
                    )
                }
            }
        }
    }
}