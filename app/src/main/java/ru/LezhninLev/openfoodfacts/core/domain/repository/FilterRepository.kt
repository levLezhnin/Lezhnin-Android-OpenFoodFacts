package ru.LezhninLev.openfoodfacts.core.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.LezhninLev.openfoodfacts.core.model.SearchFilters

interface FilterRepository {
    fun observeFilters(): Flow<SearchFilters>

    suspend fun save(filters: SearchFilters)
}
