package ru.LezhninLev.openfoodfacts.core.data.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.LezhninLev.openfoodfacts.core.data.dto.ProductDetailResponseDto
import ru.LezhninLev.openfoodfacts.core.data.dto.SearchResponseDto

interface OpenFoodFactsApi {

    @GET("api/v2/search")
    suspend fun searchProducts(
        @Query("categories_tags_en") category: String?,
        @Query("brands_tags") brand: String?,
        @Query("nutrition_grades_tags") grade: String?,
        @Query("page_size") pageSize: Int,
        @Query("page") page: Int,
        @Query("fields") fields: String = "code,product_name,brands,image_url,nutriscore_grade,categories_tags"
    ): SearchResponseDto

    @GET("api/v2/product/{barcode}.json")
    suspend fun getProductDetails(
        @Path("barcode") barcode: String
    ): ProductDetailResponseDto
}