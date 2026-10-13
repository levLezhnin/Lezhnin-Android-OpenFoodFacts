package ru.LezhninLev.openfoodfacts.core.domain.usecase

import ru.LezhninLev.openfoodfacts.core.domain.repository.ProductRepository
import ru.LezhninLev.openfoodfacts.core.model.Product

class GetProductDetailsUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(barcode: String): Product? {
        return repository.getProductDetails(barcode)
    }
}
