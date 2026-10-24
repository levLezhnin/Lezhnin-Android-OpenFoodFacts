package ru.LezhninLev.openfoodfacts.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.LezhninLev.openfoodfacts.core.model.Product

@Entity(tableName = "favorites")
data class FavoriteProductEntity(
    @PrimaryKey val code: String,
    val productName: String,
    val brands: String,
    val nutriscoreGrade: String?,
    val imageUrl: String?,
    val categoriesTags: List<String>
) {
    fun toDomain(): Product = Product(
        code = code,
        productName = productName,
        brands = brands,
        nutriscoreGrade = nutriscoreGrade,
        imageUrl = imageUrl,
        categoriesTags = categoriesTags
    )
}

fun Product.toEntity(): FavoriteProductEntity = FavoriteProductEntity(
    code = code,
    productName = productName,
    brands = brands,
    nutriscoreGrade = nutriscoreGrade,
    imageUrl = imageUrl,
    categoriesTags = categoriesTags
)