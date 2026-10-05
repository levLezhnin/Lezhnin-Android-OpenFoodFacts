package ru.LezhninLev.openfoodfacts.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ru.LezhninLev.openfoodfacts.feature.detail.DetailScreen
import ru.LezhninLev.openfoodfacts.feature.list.ListScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String = Routes.LIST
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.LIST) {
            ListScreen(
                onProductClick = { barcode ->
                    navController.navigate(Routes.detail(barcode))
                }
            )
        }

        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument("barcode") { type = NavType.StringType })
        ) { backStackEntry ->
            val barcode = backStackEntry.arguments?.getString("barcode") ?: ""
            DetailScreen(
                barcode = barcode,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.SEARCH) {}
        composable(Routes.FAVORITES) {}
    }
}
