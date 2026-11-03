package ru.LezhninLev.openfoodfacts.core.data.datastore

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.LezhninLev.openfoodfacts.core.data.local.converter.Converters
import ru.LezhninLev.openfoodfacts.core.data.local.dao.FavoriteProductDao
import ru.LezhninLev.openfoodfacts.core.data.local.entity.FavoriteProductEntity

@Database(
    entities = [FavoriteProductEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteProductDao(): FavoriteProductDao
}