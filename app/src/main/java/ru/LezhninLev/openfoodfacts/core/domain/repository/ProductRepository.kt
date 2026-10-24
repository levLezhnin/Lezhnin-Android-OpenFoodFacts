package ru.LezhninLev.openfoodfacts.core.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.LezhninLev.openfoodfacts.core.model.Product
import ru.LezhninLev.openfoodfacts.core.model.SearchFilters

interface ProductRepository {
    /**
     * Returns a flow of products for the given page and page size.
     * Using Flow allows for easy implementation of caching/reactivity.
     */
    fun getProducts(
        page: Int,
        pageSize: Int,
        filters: SearchFilters = SearchFilters()
    ): Flow<List<Product>>

    /**
     * Returns a single product by its barcode.
     */
    suspend fun getProductDetails(barcode: String): Product?

    suspend fun getAvailableCategories(): List<String>
}