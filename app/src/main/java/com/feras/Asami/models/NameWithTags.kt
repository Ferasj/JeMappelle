package com.feras.Asami.models

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class NameWithTags(
    @Embedded
    val name: Name,
    @Relation(
        parentColumn = "nameId",
        entityColumn = "tagId",
        associateBy = Junction(value = TagInName::class)
    )
    val listOfTag : List<Tag>
)
