package ru.LezhninLev.openfoodfacts.core.domain.usecase

import ru.LezhninLev.openfoodfacts.core.domain.repository.FavoritesRepository
import ru.LezhninLev.openfoodfacts.core.model.Product

class GetFavoriteByCodeUseCase(private val repository: FavoritesRepository) {
    suspend operator fun invoke(code: String): Product? = repository.getByCode(code)
}