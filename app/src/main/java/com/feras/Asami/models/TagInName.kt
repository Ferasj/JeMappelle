package com.feras.Asami.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "tagInName_table",
    primaryKeys = ["nameId", "tagId"],
    foreignKeys = [
        ForeignKey(
            entity = Name::class,
            parentColumns = ["nameId"],
            childColumns = ["nameId"]
        ),
        ForeignKey(
            entity = Tag::class,
            parentColumns = ["tagId"],
            childColumns = ["tagId"]
        )
    ]
)
data class TagInName(
    @ColumnInfo(name = "nameId")
    var nameId: Long = 0L,
    @ColumnInfo(name = "tagId")
    var tagId: Long = 0L

)