package ru.LezhninLev.openfoodfacts.core.data.local.converter

import androidx.room.TypeConverter
import com.google.gson.Gson

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromList(list: List<String>?): String =
        gson.toJson(list ?: emptyList<String>())

    @TypeConverter
    fun toList(value: String?): List<String> {
        if (value.isNullOrBlank()) return emptyList()
        return try {
            gson.fromJson(value, Array<String>::class.java).toList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}