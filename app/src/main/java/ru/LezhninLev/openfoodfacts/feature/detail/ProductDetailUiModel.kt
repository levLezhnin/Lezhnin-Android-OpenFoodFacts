package ru.LezhninLev.openfoodfacts.feature.detail

import ru.LezhninLev.openfoodfacts.core.model.Product

data class ProductDetailUiModel(
    val product: Product,
    val barcodeLabel: String,
    val nutriScore: NutriScoreUiModel?
)

data class NutriScoreUiModel(
    val label: String,
    val colorArgb: Long
)
