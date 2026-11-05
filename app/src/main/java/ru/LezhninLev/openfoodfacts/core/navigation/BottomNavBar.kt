package ru.LezhninLev.openfoodfacts.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Routes.LIST, "Products", Icons.AutoMirrored.Filled.List),
    BottomNavItem(Routes.SEARCH, "Search", Icons.Default.Search),
    BottomNavItem(Routes.FAVORITES, "Favorites", Icons.Default.Favorite),
    BottomNavItem(Routes.PROFILE, "Профиль", Icons.Default.Person)
)

object Routes {
    const val LIST = "list"
    const val DETAIL = "detail/{barcode}"
    const val SEARCH = "search"
    const val FAVORITES = "favorites"
    const val PROFILE_GRAPH = "profile_graph"
    const val PROFILE = "profile"
    const val PROFILE_EDIT = "profile/edit"

    fun detail(barcode: String) = "detail/$barcode"
}
