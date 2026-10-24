package ru.LezhninLev.openfoodfacts.core.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.LezhninLev.openfoodfacts.core.di.LocalAppContainer
import ru.LezhninLev.openfoodfacts.feature.detail.DetailScreen
import ru.LezhninLev.openfoodfacts.feature.detail.DetailViewModel
import ru.LezhninLev.openfoodfacts.feature.favorites.FavoritesScreen
import ru.LezhninLev.openfoodfacts.feature.favorites.FavoritesViewModel
import ru.LezhninLev.openfoodfacts.feature.filter.FilterScreen
import ru.LezhninLev.openfoodfacts.feature.filter.FilterViewModel
import ru.LezhninLev.openfoodfacts.feature.list.ListScreen
import ru.LezhninLev.openfoodfacts.feature.list.ListViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String = Routes.LIST
) {
    val appContainer = LocalAppContainer.current

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.LIST) {
            val viewModel: ListViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return ListViewModel(
                            appContainer.getProductsUseCase,
                            appContainer.filterPreferences
                        ) as T
                    }
                }
            )
            ListScreen(
                onProductClick = { barcode ->
                    navController.navigate(Routes.detail(barcode))
                },
                onOpenFilters = { navController.navigate(Routes.SEARCH) },
                filterBadgeCache = appContainer.filterBadgeCache,
                viewModel = viewModel
            )
        }

        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument("barcode") { type = NavType.StringType })
        ) { backStackEntry ->
            val barcode = backStackEntry.arguments?.getString("barcode") ?: ""
            val viewModel: DetailViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return DetailViewModel(
                            appContainer.getProductDetailsUseCase,
                            appContainer.isFavoriteUseCase,
                            appContainer.toggleFavoriteUseCase,
                            appContainer.getFavoriteByCodeUseCase
                        ) as T
                    }
                }
            )
            DetailScreen(
                barcode = barcode,
                onBack = { navController.popBackStack() },
                viewModel = viewModel
            )
        }

        composable(Routes.SEARCH) {
            val viewModel: FilterViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return FilterViewModel(
                            appContainer.filterPreferences,
                            appContainer.getCategoriesUseCase
                        ) as T
                    }
                }
            )
            FilterScreen(
                viewModel = viewModel,
                onClose = { navController.popBackStack() }
            )
        }

        composable(Routes.FAVORITES) {
            val viewModel: FavoritesViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return FavoritesViewModel(appContainer.getFavoritesUseCase) as T
                    }
                }
            )
            FavoritesScreen(
                onProductClick = { barcode -> navController.navigate(Routes.detail(barcode)) },
                viewModel = viewModel
            )
        }
    }
}