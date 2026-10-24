package ru.LezhninLev.openfoodfacts.core.data.network

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.LezhninLev.openfoodfacts.core.domain.repository.ProductRepository
import ru.LezhninLev.openfoodfacts.core.model.Product
import ru.LezhninLev.openfoodfacts.core.model.SearchFilters

class ProductRepositoryImpl(
    private val api: OpenFoodFactsApi
) : ProductRepository {

    override fun getProducts(
        page: Int,
        pageSize: Int,
        filters: SearchFilters
    ): Flow<List<Product>> = flow {
        val response = api.searchProducts(
            category = filters.category?.takeIf { it.isNotBlank() },
            brand = filters.brand?.takeIf { it.isNotBlank() },
            grade = filters.grade?.takeIf { it.isNotBlank() },
            pageSize = pageSize,
            page = page
        )
        emit(response.products?.map { it.toDomain() } ?: emptyList())
    }

    override suspend fun getProductDetails(barcode: String): Product? =
        try {
            api.getProductDetails(barcode).product?.toDomain()
        } catch (e: Exception) {
            null
        }

    override suspend fun getAvailableCategories(): List<String> {
        val response = api.searchProducts(
            category = null,
            brand = null,
            grade = null,
            pageSize = 100,
            page = 1,
            fields = "categories_tags"
        )
        val counts = mutableMapOf<String, Int>()
        response.products?.forEach { p ->
            p.categoriesTags?.forEach { raw ->
                if (raw.startsWith("en:")) {
                    val nice = raw.removePrefix("en:")
                        .replace('-', ' ')
                        .replaceFirstChar { it.uppercase() }
                    counts[nice] = (counts[nice] ?: 0) + 1
                }
            }
        }
        return counts.entries
            .sortedByDescending { it.value }
            .take(30)
            .map { it.key }
    }
}