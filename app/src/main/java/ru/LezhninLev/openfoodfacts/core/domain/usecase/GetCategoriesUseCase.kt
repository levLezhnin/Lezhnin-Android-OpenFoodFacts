package ru.LezhninLev.openfoodfacts.core.domain.usecase

import ru.LezhninLev.openfoodfacts.core.domain.repository.ProductRepository

class GetCategoriesUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(): List<String> = repository.getAvailableCategories()
}