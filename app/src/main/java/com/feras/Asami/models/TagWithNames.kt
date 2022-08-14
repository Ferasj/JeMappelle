package com.feras.Asami.models

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class TagWithNames(
    @Embedded
    val tag : Tag,
    @Relation(
        parentColumn = "tagId",
        entityColumn = "nameId",
        associateBy = Junction(value = TagInName::class)
    )
    val listOfName: List<Name>
)
