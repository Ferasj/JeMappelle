package com.feras.Asami.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.feras.Asami.database.NamesDao
import com.feras.Asami.models.NameWithTags
import kotlinx.coroutines.launch

class NameDetailsViewModel(private val dao: NamesDao, private val nameId : Long) : ViewModel() {

    lateinit var currentNameDetails : LiveData<NameWithTags>

    init {
        viewModelScope.launch {
            currentNameDetails = dao.getNamesWithTagByNameId(nameId)
        }
    }

    fun deleteName(nameId : Long){
        viewModelScope.launch {
            dao.deleteTagInNamesWithNameId(nameId)

        }
        viewModelScope.launch {
            dao.deleteNameWithId(nameId)
        }
    }
}