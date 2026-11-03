package ru.LezhninLev.openfoodfacts.core.domain.usecase

import ru.LezhninLev.openfoodfacts.core.domain.repository.FilterRepository
import ru.LezhninLev.openfoodfacts.core.model.SearchFilters

class SaveFiltersUseCase(private val repository: FilterRepository) {
    suspend operator fun invoke(filters: SearchFilters) = repository.save(filters)
}
