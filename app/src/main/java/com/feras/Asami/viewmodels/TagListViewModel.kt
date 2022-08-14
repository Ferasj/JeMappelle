package com.feras.Asami.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.feras.Asami.database.NamesDao
import com.feras.Asami.models.Tag
import kotlinx.coroutines.launch

class TagListViewModel(private val dao : NamesDao) : ViewModel() {

    lateinit var listOfTags : LiveData<List<Tag>>


    init {
        viewModelScope.launch {
            listOfTags = dao.getAllTags()
        }
    }

    fun deleteTag(tagId : Long){
        viewModelScope.launch {
            dao.deleteTagConnections(tagId)
            dao.deleteTagById(tagId)
        }
    }

    fun searchFor(tagName : String) : LiveData<List<Tag>>{
        return dao.getTagsByName(tagName)
    }
}