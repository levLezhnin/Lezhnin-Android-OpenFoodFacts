package ru.LezhninLev.openfoodfacts.core.domain.usecase

import kotlinx.coroutines.flow.Flow
import ru.LezhninLev.openfoodfacts.core.domain.repository.FavoritesRepository
import ru.LezhninLev.openfoodfacts.core.model.Product

class GetFavoritesUseCase(private val repository: FavoritesRepository) {
    operator fun invoke(): Flow<List<Product>> = repository.getAll()
}