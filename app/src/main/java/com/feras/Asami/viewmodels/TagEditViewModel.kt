package com.feras.Asami.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.feras.Asami.database.NamesDao
import com.feras.Asami.models.Tag
import kotlinx.coroutines.launch

class TagEditViewModel(private val dao : NamesDao, private val tagId : Long) : ViewModel(){

    lateinit var tag : LiveData<Tag>

    init {
        viewModelScope.launch {
            tag = dao.getTagById(tagId)
        }
    }

    fun updateTag(tag : Tag){
        viewModelScope.launch {
            dao.saveTag(tag)
        }
    }
}