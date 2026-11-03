package ru.LezhninLev.openfoodfacts.core.data.datastore

import kotlinx.coroutines.flow.Flow
import ru.LezhninLev.openfoodfacts.core.domain.repository.FilterRepository
import ru.LezhninLev.openfoodfacts.core.model.SearchFilters

class FilterRepositoryImpl(
    private val filterPreferences: FilterPreferences
) : FilterRepository {
    override fun observeFilters(): Flow<SearchFilters> = filterPreferences.filtersFlow

    override suspend fun save(filters: SearchFilters) = filterPreferences.save(filters)
}
