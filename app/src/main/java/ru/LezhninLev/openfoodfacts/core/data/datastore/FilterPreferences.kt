package ru.LezhninLev.openfoodfacts.core.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import ru.LezhninLev.openfoodfacts.core.model.SearchFilters
import java.io.IOException

private val Context.filterDataStore: DataStore<Preferences> by preferencesDataStore("filters")

class FilterPreferences(private val context: Context) {

    private val dataStore = context.applicationContext.filterDataStore

    val filtersFlow: Flow<SearchFilters> = dataStore.data
        .catch { e ->
            if (e is IOException) emit(emptyPreferences()) else throw e
        }
        .map { prefs ->
            SearchFilters(
                category = prefs[KEY_CATEGORY]?.takeIf { it.isNotBlank() },
                brand = prefs[KEY_BRAND]?.takeIf { it.isNotBlank() },
                grade = prefs[KEY_GRADE]?.takeIf { it.isNotBlank() }
            )
        }

    suspend fun save(filters: SearchFilters) {
        dataStore.edit { prefs ->
            if (!filters.category.isNullOrBlank()) prefs[KEY_CATEGORY] = filters.category
            else prefs.remove(KEY_CATEGORY)

            if (!filters.brand.isNullOrBlank()) prefs[KEY_BRAND] = filters.brand
            else prefs.remove(KEY_BRAND)

            if (!filters.grade.isNullOrBlank()) prefs[KEY_GRADE] = filters.grade
            else prefs.remove(KEY_GRADE)
        }
    }

    private companion object {
        val KEY_CATEGORY = stringPreferencesKey("filter_category")
        val KEY_BRAND = stringPreferencesKey("filter_brand")
        val KEY_GRADE = stringPreferencesKey("filter_grade")
    }
}