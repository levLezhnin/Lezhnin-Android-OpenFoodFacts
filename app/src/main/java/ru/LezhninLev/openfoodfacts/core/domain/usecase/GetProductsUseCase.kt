package ru.LezhninLev.openfoodfacts.core.domain.usecase

import kotlinx.coroutines.flow.Flow
import ru.LezhninLev.openfoodfacts.core.domain.repository.ProductRepository
import ru.LezhninLev.openfoodfacts.core.model.Product

class GetProductsUseCase(private val repository: ProductRepository) {
    operator fun invoke(page: Int, pageSize: Int): Flow<List<Product>> {
        return repository.getProducts(page, pageSize)
    }
}
