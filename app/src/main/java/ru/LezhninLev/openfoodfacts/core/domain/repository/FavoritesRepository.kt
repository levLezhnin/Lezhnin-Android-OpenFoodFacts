package ru.LezhninLev.openfoodfacts.core.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.LezhninLev.openfoodfacts.core.model.Product

interface FavoritesRepository {
    fun getAll(): Flow<List<Product>>
    suspend fun getByCode(code: String): Product?
    fun isFavorite(code: String): Flow<Boolean>
    suspend fun isFavoriteOnce(code: String): Boolean
    suspend fun add(product: Product)
    suspend fun remove(code: String)
}