package ru.LezhninLev.openfoodfacts.core.domain.repository

import ru.LezhninLev.openfoodfacts.core.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    /**
     * Returns a flow of products for the given page and page size.
     * Using Flow allows for easy implementation of caching/reactivity.
     */
    fun getProducts(page: Int, pageSize: Int, category: String? = null): Flow<List<Product>>

    /**
     * Returns a single product by its barcode.
     */
    suspend fun getProductDetails(barcode: String): Product?
}
