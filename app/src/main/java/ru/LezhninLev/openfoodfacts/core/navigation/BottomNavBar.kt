package ru.LezhninLev.openfoodfacts.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Routes.LIST, "Products", Icons.Default.List),
    BottomNavItem(Routes.SEARCH, "Search", Icons.Default.Search),
    BottomNavItem(Routes.FAVORITES, "Favorites", Icons.Default.Favorite)
)

object Routes {
    const val LIST = "list"
    const val DETAIL = "detail/{barcode}"
    const val SEARCH = "search"
    const val FAVORITES = "favorites"

    fun detail(barcode: String) = "detail/$barcode"
}
