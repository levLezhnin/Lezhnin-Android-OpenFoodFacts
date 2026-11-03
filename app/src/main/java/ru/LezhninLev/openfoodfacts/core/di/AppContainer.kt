package ru.LezhninLev.openfoodfacts.core.di

import android.content.Context
import ru.LezhninLev.openfoodfacts.core.data.network.NetworkModule
import ru.LezhninLev.openfoodfacts.core.data.network.OpenFoodFactsApi
import ru.LezhninLev.openfoodfacts.core.data.network.ProductRepositoryImpl
import ru.LezhninLev.openfoodfacts.core.domain.repository.ProductRepository
import ru.LezhninLev.openfoodfacts.core.domain.usecase.GetProductDetailsUseCase
import ru.LezhninLev.openfoodfacts.core.domain.usecase.GetProductsUseCase

class AppContainer(context: Context) {
    private val api: OpenFoodFactsApi = NetworkModule.provideOpenFoodFactsApi(context)
    private val repository: ProductRepository = ProductRepositoryImpl(api)

    val getProductsUseCase = GetProductsUseCase(repository)
    val getProductDetailsUseCase = GetProductDetailsUseCase(repository)
}
