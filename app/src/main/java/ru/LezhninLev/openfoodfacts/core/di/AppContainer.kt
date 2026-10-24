package ru.LezhninLev.openfoodfacts.core.di

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import ru.LezhninLev.openfoodfacts.core.cache.FilterBadgeCache
import ru.LezhninLev.openfoodfacts.core.data.datastore.FilterPreferences
import ru.LezhninLev.openfoodfacts.core.data.datastore.AppDatabase
import ru.LezhninLev.openfoodfacts.core.data.local.repository.FavoritesRepositoryImpl
import ru.LezhninLev.openfoodfacts.core.data.network.NetworkModule
import ru.LezhninLev.openfoodfacts.core.data.network.OpenFoodFactsApi
import ru.LezhninLev.openfoodfacts.core.data.network.ProductRepositoryImpl
import ru.LezhninLev.openfoodfacts.core.domain.repository.FavoritesRepository
import ru.LezhninLev.openfoodfacts.core.domain.repository.ProductRepository
import ru.LezhninLev.openfoodfacts.core.domain.usecase.GetCategoriesUseCase
import ru.LezhninLev.openfoodfacts.core.domain.usecase.GetFavoriteByCodeUseCase
import ru.LezhninLev.openfoodfacts.core.domain.usecase.GetFavoritesUseCase
import ru.LezhninLev.openfoodfacts.core.domain.usecase.GetProductDetailsUseCase
import ru.LezhninLev.openfoodfacts.core.domain.usecase.GetProductsUseCase
import ru.LezhninLev.openfoodfacts.core.domain.usecase.IsFavoriteUseCase
import ru.LezhninLev.openfoodfacts.core.domain.usecase.ToggleFavoriteUseCase

class AppContainer(context: Context) {

    private val appContext = context.applicationContext
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val api: OpenFoodFactsApi = NetworkModule.provideOpenFoodFactsApi(appContext)
    private val productRepository: ProductRepository = ProductRepositoryImpl(api)

    private val database: AppDatabase = Room.databaseBuilder(
        appContext,
        AppDatabase::class.java,
        "food-facts.db"
    ).build()
    private val favoritesRepository: FavoritesRepository =
        FavoritesRepositoryImpl(database.favoriteProductDao())

    val filterPreferences = FilterPreferences(appContext)
    val filterBadgeCache = FilterBadgeCache(filterPreferences, appScope)

    val getProductsUseCase = GetProductsUseCase(productRepository)
    val getProductDetailsUseCase = GetProductDetailsUseCase(productRepository)
    val getCategoriesUseCase = GetCategoriesUseCase(productRepository)

    val getFavoritesUseCase = GetFavoritesUseCase(favoritesRepository)
    val isFavoriteUseCase = IsFavoriteUseCase(favoritesRepository)
    val toggleFavoriteUseCase = ToggleFavoriteUseCase(favoritesRepository)
    val getFavoriteByCodeUseCase = GetFavoriteByCodeUseCase(favoritesRepository)
}