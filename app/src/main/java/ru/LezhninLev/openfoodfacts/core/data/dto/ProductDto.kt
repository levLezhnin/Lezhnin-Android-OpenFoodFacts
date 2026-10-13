package ru.LezhninLev.openfoodfacts.core.data.dto

import com.google.gson.annotations.SerializedName
import ru.LezhninLev.openfoodfacts.core.model.Product

data class ProductDto(
    @SerializedName("code") val code: String?,
    @SerializedName("product_name") val productName: String?,
    @SerializedName("brands") val brands: String?,
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("nutriscore_grade") val nutriscoreGrade: String?,
    @SerializedName("categories_tags") val categoriesTags: List<String>?
) {
    fun toDomain(): Product {
        return Product(
            code = code ?: "",
            productName = productName ?: "Unknown Product",
            brands = brands ?: "Unknown Brand",
            nutriscoreGrade = nutriscoreGrade,
            imageUrl = imageUrl,
            categoriesTags = categoriesTags ?: emptyList()
        )
    }
}

data class SearchResponseDto(
    @SerializedName("products") val products: List<ProductDto>?,
    @SerializedName("page") val page: Int?,
    @SerializedName("pages") val pages: Int?,
    @SerializedName("count") val count: Int?
)

data class ProductDetailResponseDto(
    @SerializedName("product") val product: ProductDto?
)
