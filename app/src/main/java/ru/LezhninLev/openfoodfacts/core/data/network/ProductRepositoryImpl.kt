package ru.LezhninLev.openfoodfacts.core.data.network

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.LezhninLev.openfoodfacts.core.domain.repository.ProductRepository
import ru.LezhninLev.openfoodfacts.core.model.Product

class ProductRepositoryImpl(
    private val api: OpenFoodFactsApi
) : ProductRepository {

    override fun getProducts(page: Int, pageSize: Int, category: String?): Flow<List<Product>> = flow {
        try {
            val response = api.searchProducts(
                category = category,
                pageSize = pageSize,
                page = page
            )
            val products = response.products?.map { it.toDomain() } ?: emptyList()
            emit(products)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getProductDetails(barcode: String): Product? {
        return try {
            val response = api.getProductDetails(barcode)
            response.product?.toDomain()
        } catch (e: Exception) {
            null
        }
    }
}
