package com.example.openfoodfacts.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.openfoodfacts.feature.detail.DetailScreen
import com.example.openfoodfacts.feature.list.ListScreen

/**
 * Основной NavHost приложения.
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String = Routes.LIST
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Экран списка
        composable(Routes.LIST) {
            ListScreen(
                onProductClick = { barcode ->
                    navController.navigate(Routes.detail(barcode))
                }
            )
        }

        // Экран деталей
        composable(
            route = Routes.DETAIL,
            arguments = listOf(androidx.navigation.navArgument("barcode") { type = androidx.navigation.NavType.StringType })
        ) { backStackEntry ->
            val barcode = backStackEntry.arguments?.getString("barcode") ?: ""
            DetailScreen(
                barcode = barcode,
                onBack = { navController.popBackStack() }
            )
        }

        // Заглушки для остальных табов
        composable(Routes.SEARCH) {
            // Placeholder
        }
        composable(Routes.FAVORITES) {
            // Placeholder
        }
    }
}
