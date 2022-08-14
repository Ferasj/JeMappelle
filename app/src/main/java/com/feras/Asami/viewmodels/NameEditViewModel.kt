package com.feras.Asami.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.feras.Asami.database.NamesDao
import com.feras.Asami.models.Name
import com.feras.Asami.models.NameWithTags
import com.feras.Asami.models.Tag
import com.feras.Asami.models.TagInName
import kotlinx.coroutines.launch

class NameEditViewModel(private val dao : NamesDao, private val nameId : Long): ViewModel() {
    lateinit var currentName : LiveData<Name>

    lateinit var listOfTagNames : LiveData<List<String>>

    lateinit var listOfTagId : LiveData<List<Long>>

    lateinit var currentNameWithTags: LiveData<NameWithTags>

    init {
        viewModelScope.launch {
            currentName = dao.getNameById(nameId)
            listOfTagNames = dao.getTagNames()
            listOfTagId = dao.getTagIds()
            currentNameWithTags = dao.getNamesWithTagByNameId(nameId)
        }
    }

    fun updateName(name: Name){
        viewModelScope.launch {
            dao.saveName(name)
        }
    }

    fun formatTagNames(currentName : NameWithTags) : LiveData<MutableList<String>>{
        val listToReturn = mutableListOf<String>()
        viewModelScope.launch {
            for (tag in currentName.listOfTag){
                listToReturn.add(tag.tagName)
            }
        }
        return MutableLiveData(listToReturn)
    }

    fun reCallTagNames() {
        viewModelScope.launch {
            listOfTagNames = dao.getTagNames()
            listOfTagId = dao.getTagIds()
        }
    }

    fun addTagInName(tagInName: TagInName) {
        viewModelScope.launch {
            dao.insertTagInName(tagInName)
        }
    }

    fun insertTag(tag: Tag) {
        viewModelScope.launch {
            dao.insertTag(tag)
        }
    }

    fun removeTagInName(tagInName: TagInName) {
        viewModelScope.launch {
            dao.deleteTagInName(tagInName)
        }
    }
}