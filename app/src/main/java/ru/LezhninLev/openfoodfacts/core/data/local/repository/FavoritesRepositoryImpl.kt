package ru.LezhninLev.openfoodfacts.core.data.local.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.LezhninLev.openfoodfacts.core.data.local.dao.FavoriteProductDao
import ru.LezhninLev.openfoodfacts.core.data.local.entity.toEntity
import ru.LezhninLev.openfoodfacts.core.domain.repository.FavoritesRepository
import ru.LezhninLev.openfoodfacts.core.model.Product

class FavoritesRepositoryImpl(
    private val dao: FavoriteProductDao
) : FavoritesRepository {

    override fun getAll(): Flow<List<Product>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    override suspend fun getByCode(code: String): Product? =
        dao.getByCode(code)?.toDomain()

    override fun isFavorite(code: String): Flow<Boolean> =
        dao.isFavoriteFlow(code)

    override suspend fun isFavoriteOnce(code: String): Boolean =
        dao.isFavoriteOnce(code)

    override suspend fun add(product: Product) =
        dao.insert(product.toEntity())

    override suspend fun remove(code: String) =
        dao.deleteByCode(code)
}