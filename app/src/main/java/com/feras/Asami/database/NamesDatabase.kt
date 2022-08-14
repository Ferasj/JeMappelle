package com.feras.Asami.database

import android.content.Context
import androidx.room.*
import com.feras.Asami.models.Name
import com.feras.Asami.models.Tag
import com.feras.Asami.models.TagInName
import com.feras.Asami.typeconverters.TagsTypeConverter

@Database(entities = [Name::class, Tag::class, TagInName::class], version = 1, exportSchema = false)
@TypeConverters(TagsTypeConverter::class)
abstract class NamesDatabase : RoomDatabase() {
    abstract val namesDao: NamesDao

    companion object {
        @Volatile
        private var INSTANCE: NamesDatabase? = null

        fun getInstance(context: Context): NamesDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        NamesDatabase::class.java,
                        "/storage/emulated/0/Asami/database.db"
                    ).build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}