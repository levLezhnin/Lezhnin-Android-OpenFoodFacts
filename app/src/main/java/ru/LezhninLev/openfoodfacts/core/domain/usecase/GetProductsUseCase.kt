package ru.LezhninLev.openfoodfacts.core.domain.usecase

import kotlinx.coroutines.flow.Flow
import ru.LezhninLev.openfoodfacts.core.domain.repository.ProductRepository
import ru.LezhninLev.openfoodfacts.core.model.Product
import ru.LezhninLev.openfoodfacts.core.model.SearchFilters

class GetProductsUseCase(private val repository: ProductRepository) {
    operator fun invoke(
        page: Int,
        pageSize: Int,
        filters: SearchFilters = SearchFilters()
    ): Flow<List<Product>> = repository.getProducts(page, pageSize, filters)
}