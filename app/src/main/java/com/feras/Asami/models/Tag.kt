package com.feras.Asami.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tags_table")
data class Tag(
    @PrimaryKey(autoGenerate = true)
    var tagId : Long = 0L,
    @ColumnInfo(name = "tag_name")
    var tagName : String = ""
)
