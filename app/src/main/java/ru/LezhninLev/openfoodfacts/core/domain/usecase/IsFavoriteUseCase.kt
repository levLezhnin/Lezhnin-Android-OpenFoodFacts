package ru.LezhninLev.openfoodfacts.core.domain.usecase

import kotlinx.coroutines.flow.Flow
import ru.LezhninLev.openfoodfacts.core.domain.repository.FavoritesRepository

class IsFavoriteUseCase(private val repository: FavoritesRepository) {
    operator fun invoke(code: String): Flow<Boolean> = repository.isFavorite(code)
}