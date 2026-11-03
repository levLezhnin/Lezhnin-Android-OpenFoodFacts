package ru.LezhninLev.openfoodfacts.core.model

data class SearchFilters(
    val category: String? = null,
    val brand: String? = null,
    val grade: String? = null
) {
    fun hasActive(): Boolean =
        !category.isNullOrBlank() || !brand.isNullOrBlank() || !grade.isNullOrBlank()
}