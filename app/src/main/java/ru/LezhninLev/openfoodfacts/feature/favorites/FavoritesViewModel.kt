package ru.LezhninLev.openfoodfacts.feature.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import ru.LezhninLev.openfoodfacts.core.domain.usecase.GetFavoritesUseCase
import ru.LezhninLev.openfoodfacts.core.model.Product

class FavoritesViewModel(
    getFavoritesUseCase: GetFavoritesUseCase
) : ViewModel() {

    val favorites: StateFlow<List<Product>> = getFavoritesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )
}