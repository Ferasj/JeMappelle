package com.feras.Asami.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.feras.Asami.models.*

@Dao
interface NamesDao {

    @Query("SELECT * FROM names_table WHERE nameId =:nameId")
    fun getNameById(nameId: Long) : LiveData<Name>

    @Delete
    suspend fun removeName(name : Name)

    @Update
    suspend fun saveTag(tag :Tag)

    @Query("SELECT * FROM tags_table WHERE tagId=:tagId")
    fun getTagById(tagId: Long) : LiveData<Tag>

    @Query("DELETE FROM tags_table WHERE tagId =:tagId")
    suspend fun deleteTagById(tagId: Long)

    @Insert
    suspend fun insertName(name : Name)

    @Insert
    suspend fun insertTag(tag : Tag)

    @Insert
    suspend fun insertTagInName(tagInName: TagInName)

    @Query("SELECT * FROM names_table ORDER BY nameId DESC")
    fun getAllNames() : LiveData<List<Name>>

    @Query("SELECT * FROM tags_table")
    fun getAllTags() : LiveData<List<Tag>>

    @Query("DELETE FROM tagInName_table WHERE tagId =:tagId")
    suspend fun deleteTagConnections(tagId: Long)

    @Query("SELECT tag_name FROM tags_table")
    fun getTagNames() : LiveData<List<String>>

    @Query("SELECT tagId FROM tags_table")
    fun getTagIds() : LiveData<List<Long>>

    @Query("DELETE FROM names_table WHERE nameId=:nameID")
    suspend fun deleteNameWithId(nameID : Long)



    @Query("DELETE FROM tagInName_table WHERE nameId=:nameId")
    suspend fun deleteTagInNamesWithNameId(nameId: Long)

    @Delete
    suspend fun deleteTagInName(tagInName: TagInName)

    @Query("SELECT tag_name FROM tags_table WHERE tagId=:tagId")
    fun getTagNameById(tagId : Long) : LiveData<String>

    @Update
    suspend fun saveName(name: Name)

    @Query("SELECT * FROM tags_table WHERE tag_name LIKE '%'||:tagsName||'%'")
    fun getTagsByName(tagsName: String) : LiveData<List<Tag>>

    @Query("SELECT * FROM names_table ORDER BY nameId DESC LIMIT 1")
    fun getCurrentAdditionName() : LiveData<Name>

    @Query("SELECT * FROM names_table WHERE name LIKE '%'||:name||'%'")
    @Transaction
    fun getNamesByName(name: String): LiveData<List<NameWithTags>>

    @Query("SELECT * FROM names_table")
    @Transaction
    fun getNamesWithTags() : LiveData<List<NameWithTags>>

    @Query("SELECT * FROM tagInName_table")
    fun getAllRelationships() : LiveData<List<TagInName>>

    @Query("SELECT * FROM names_table WHERE nameId=:nameId")
    @Transaction
    fun getNamesWithTagByNameId(nameId : Long) : LiveData<NameWithTags>


    @Query("SELECT names_table.* FROM names_table JOIN tagInName_table ON names_table.nameId = tagInName_table.nameId JOIN tags_table ON tagInName_table.tagId = tags_table.tagId WHERE tags_table.tagId=:tagId ")
    @Transaction
    fun getNameWithTagsByTagId(tagId: Long): LiveData<List<NameWithTags>>


    @Query("DELETE FROM tagInName_table WHERE tagId=:tagId")
    suspend fun deleteTagFromTagInNameWithId(tagId: Long)



    @Query("SELECT * FROM names_table ORDER BY name ASC")
    @Transaction
    fun getNamesWithTagsNameASC() : LiveData<List<NameWithTags>>

    @Query("SELECT * FROM names_table ORDER BY name DESC")
    @Transaction
    fun getNamesWithTagsNameDES() : LiveData<List<NameWithTags>>

    @Query("SELECT * FROM names_table ORDER BY date_added ASC")
    @Transaction
    fun getNamesWithTagsDateAddedASC() : LiveData<List<NameWithTags>>

    @Query("SELECT * FROM names_table ORDER BY date_added DESC")
    @Transaction
    fun getNamesWithTagsDateAddedDES() : LiveData<List<NameWithTags>>


//uncomment
    @Query("SELECT * FROM names_table WHERE notes LIKE '%'||:notesSubString||'%'")
    @Transaction
    fun getNameWithTagsByTagName(notesSubString: String): LiveData<List<NameWithTags>>



    @Query("SELECT names_table.* FROM names_table WHERE name LIKE '%'||:keyword||'%' OR notes LIKE '%'||:keyword||'%' UNION SELECT names_table.* FROM names_table JOIN tagInName_table ON names_table.nameId = tagInName_table.nameId JOIN tags_table ON tagInName_table.tagId = tags_table.tagId WHERE tags_table.tag_name LIKE '%'||:keyword||'%'")
    @Transaction
    fun searchNamesByKeyword(keyword: String): LiveData<List<NameWithTags>>




}










