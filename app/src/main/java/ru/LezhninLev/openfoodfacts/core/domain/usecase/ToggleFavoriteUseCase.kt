package ru.LezhninLev.openfoodfacts.core.domain.usecase

import ru.LezhninLev.openfoodfacts.core.domain.repository.FavoritesRepository
import ru.LezhninLev.openfoodfacts.core.model.Product

class ToggleFavoriteUseCase(private val repository: FavoritesRepository) {
    suspend operator fun invoke(product: Product) {
        if (repository.isFavoriteOnce(product.code)) {
            repository.remove(product.code)
        } else {
            repository.add(product)
        }
    }
}