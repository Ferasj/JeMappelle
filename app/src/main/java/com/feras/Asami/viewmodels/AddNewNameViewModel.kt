package com.feras.Asami.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.feras.Asami.database.NamesDao
import com.feras.Asami.models.Name
import com.feras.Asami.models.Tag
import com.feras.Asami.models.TagInName
import kotlinx.coroutines.launch

class AddNewNameViewModel(private val dao: NamesDao) : ViewModel() {

    lateinit var listOfTagNames : LiveData<List<String>>
    lateinit var currentName : LiveData<Name>


    lateinit var listOfTagId : LiveData<List<Long>>

    init {
        viewModelScope.launch {
            listOfTagNames = dao.getTagNames()
            currentName = dao.getCurrentAdditionName()
            listOfTagId = dao.getTagIds()
        }
    }


    fun insertTag(tag: Tag){
        viewModelScope.launch {
            dao.insertTag(tag)
        }
    }


    fun addTagInName(tagInName: TagInName){
        viewModelScope.launch {
            dao.insertTagInName(tagInName)
        }
    }


    fun deleteFromTagInTableWithNameId(nameId : Long){
        viewModelScope.launch {
            dao.deleteTagInNamesWithNameId(nameId)
            dao.deleteNameWithId(nameId)
        }
    }

    fun removeTagInName(tagInName: TagInName){
        viewModelScope.launch {
            dao.deleteTagInName(tagInName)
        }
    }

    fun saveName(name: Name){
        viewModelScope.launch {
            dao.saveName(name)
        }
    }


    fun reCallTagNames(){
        viewModelScope.launch {
            listOfTagNames = dao.getTagNames()
            listOfTagId = dao.getTagIds()
        }
    }



}