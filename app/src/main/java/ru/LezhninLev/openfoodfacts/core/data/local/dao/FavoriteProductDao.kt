package ru.LezhninLev.openfoodfacts.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.LezhninLev.openfoodfacts.core.data.local.entity.FavoriteProductEntity

@Dao
interface FavoriteProductDao {

    @Query("SELECT * FROM favorites ORDER BY productName ASC")
    fun getAll(): Flow<List<FavoriteProductEntity>>

    @Query("SELECT * FROM favorites WHERE code = :code LIMIT 1")
    suspend fun getByCode(code: String): FavoriteProductEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE code = :code)")
    fun isFavoriteFlow(code: String): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE code = :code)")
    suspend fun isFavoriteOnce(code: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: FavoriteProductEntity)

    @Query("DELETE FROM favorites WHERE code = :code")
    suspend fun deleteByCode(code: String)
}