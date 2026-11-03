package ru.LezhninLev.openfoodfacts.core.domain.usecase

import kotlinx.coroutines.flow.Flow
import ru.LezhninLev.openfoodfacts.core.domain.repository.FilterRepository
import ru.LezhninLev.openfoodfacts.core.model.SearchFilters

class ObserveFiltersUseCase(private val repository: FilterRepository) {
    operator fun invoke(): Flow<SearchFilters> = repository.observeFilters()
}
