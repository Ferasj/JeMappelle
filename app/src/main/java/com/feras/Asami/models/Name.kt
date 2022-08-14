package com.feras.Asami.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "names_table")
data class Name(
    @PrimaryKey(autoGenerate = true)
    var nameId : Long = 0L,
    @ColumnInfo(name = "name")
    var name : String = "",
    @ColumnInfo(name = "notes")
    var notes: String="",
    @ColumnInfo(name = "date_added")
    var dateAdded : String = "",
    @ColumnInfo(name = "date_modified")
    var dateModified : String = ""

)

