package com.example.openfoodfacts.core.data

import com.example.openfoodfacts.core.model.Product
import kotlinx.coroutines.delay

/**
 * Репозиторий, предоставляющий разнообразные моковые данные для имитации работы с API Open Food Facts.
 * Теперь включает разные категории продуктов: Кофе, Молочные продукты, Фрукты, Снеки.
 */
object MockProductRepository {

    private val allProducts = listOf(
        // --- COFFEE ---
        Product("1", "Espresso Roast", "Coffee Co", "A", "https://images.unsplash.com/photo-1510591509098-f4fdc6d0ff04?w=500", listOf("Dark Roast", "Arabica")),
        Product("2", "Morning Blend", "SunRise", "B", "https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?w=500", listOf("Light Roast", "Breakfast")),

        // --- DAIRY ---
        Product("3", "Organic Whole Milk", "DairyFarm", "A", "https://images.unsplash.com/photo-1563636619-e9107da5a1bb?w=500", listOf("Dairy", "Organic")),
        Product("4", "Greek Yogurt Strawberry", "FreshCream", "B", "https://images.unsplash.com/photo-1488477181946-6428a0291777?w=500", listOf("Yogurt", "Fruit")),
        Product("5", "Cheddar Cheese Block", "CheeseMaster", "C", "https://images.unsplash.com/photo-1486297664445-39363d6d636c?w=500", listOf("Cheese", "Dairy")),

        // --- FRUITS & VEGGIES ---
        Product("6", "Red Gala Apples", "NaturePick", "A", "https://images.unsplash.com/photo-1560806887-1e4cd0b6cbd6?w=500", listOf("Fruit", "Fresh")),
        Product("7", "Organic Bananas", "TropicalFruit", "A", "https://images.unsplash.com/photo-1571771894821-ad9902412746?w=500", listOf("Fruit", "Tropical")),
        Product("8", "Avocado Hass", "GreenGarden", "B", "https://images.unsplash.com/photo-1523049773299-91f2f53fd43c?w=500", listOf("Vegetable", "Healthy")),

        // --- SNACKS ---
        Product("9", "Potato Chips Salted", "CrunchyCo", "D", "https://images.unsplash.com/photo-1566478430549-4333b444330a?w=500", listOf("Snack", "Salty")),
        Product("10", "Dark Chocolate 70%", "CocoaRich", "B", "https://images.unsplash.com/photo-1511381939415-e44015466834?w=500", listOf("Snack", "Sweet", "Chocolate")),
        Product("11", "Almond Granola Bar", "EnergyWay", "B", "https://images.unsplash.com/photo-1590080875515-8a3a8dc56382?w=500", listOf("Snack", "Healthy", "Cereal")),

        // --- BEVERAGES (NON-COFFEE) ---
        Product("12", "Orange Juice Pure", "FruitSqueeze", "A", "https://images.unsplash.com/photo-1613478223719-626a96fa8a1d?w=500", listOf("Juice", "Fruit")),
        Product("13", "Sparkling Water", "AquaPure", "A", "https://images.unsplash.com/photo-1559839914-17aae19cea71?w=500", listOf("Beverage", "Water")),

        // --- ADDITIONAL SAMPLES TO FILL PAGES ---
        Product("14", "Honey Jar", "BeeSweet", "A", "https://images.unsplash.com/photo-1587049352846-4a222e784d38?w=500", listOf("Sweetener", "Natural")),
        Product("15", "Whole Grain Bread", "BakeryFresh", "B", "https://images.unsplash.com/photo-1509440159596-0249088772ff?w=500", listOf("Bakery", "Grain")),
        Product("16", "Frozen Blueberries", "FrostyFruit", "A", "https://images.unsplash.com/photo-1498557850523-fd3d118b962e?w=500", listOf("Frozen", "Fruit")),
        Product("17", "Peanut Butter Creamy", "NuttySpread", "C", "https://images.unsplash.com/photo-1590301157890-4810ed382f60?w=500", listOf("Spread", "Nut")),
        Product("18", "Oat Milk Barista", "OatLife", "A", "https://images.unsplash.com/photo-1550583724-1255d1b2864c?w=500", listOf("Dairy Alternative", "Oat")),
        Product("19", "Pretzels Salted", "SnackTime", "D", "https://images.unsplash.com/photo-1599599810769-bcde5a160d32?w=500", listOf("Snack", "Salty")),
        Product("20", "Green Tea Bags", "LeafyBrew", "A", "https://images.unsplash.com/photo-1564890369478-c89ca6d9cde9?w=500", listOf("Tea", "Beverage")),
    )

    /**
     * Имитирует сетевой запрос списка продуктов с пагинацией.
     */
    suspend fun getProducts(page: Int, pageSize: Int = 10): List<Product> {
        delay(1000) // Имитация задержки сети
        val start = (page - 1) * pageSize
        if (start >= allProducts.size) return emptyList()
        val end = minOf(start + pageSize, allProducts.size)
        return allProducts.subList(start, end)
    }

    /**
     * Имитирует сетевой запрос деталей продукта по его коду (barcode).
     */
    suspend fun getProductDetails(code: String): Product? {
        delay(800) // Имитация задержки сети
        return allProducts.find { it.code == code }
    }
}
