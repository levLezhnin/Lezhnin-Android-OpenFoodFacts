package ru.LezhninLev.openfoodfacts.core.model

/**
 * Доменная модель продукта, соответствующая структуре Open Food Facts.
 */
data class Product(
    val code: String,
    val productName: String,
    val brands: String,
    val nutriscoreGrade: String?,
    val imageUrl: String?,
    val categoriesTags: List<String>
)
