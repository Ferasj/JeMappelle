package com.feras.Asami.typeconverters

import androidx.room.TypeConverter
import com.feras.Asami.models.Tag
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*


public class TagsTypeConverter {
    var gson = Gson()

    @TypeConverter
    fun stringToSomeObjectList(data: String?): List<Tag?>? {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType = object : TypeToken<List<Tag?>?>() {}.type
        return gson.fromJson<List<Tag?>>(data, listType)
    }

    @TypeConverter
    fun someObjectListToString(someObjects: List<Tag?>?): String? {
        return gson.toJson(someObjects)
    }
}