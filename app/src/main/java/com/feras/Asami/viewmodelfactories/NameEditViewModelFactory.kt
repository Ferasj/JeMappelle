package com.feras.Asami.viewmodelfactories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.feras.Asami.database.NamesDao
import com.feras.Asami.viewmodels.NameEditViewModel
import java.lang.IllegalArgumentException

class NameEditViewModelFactory(private val dao : NamesDao, private val nameId : Long) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(NameEditViewModel::class.java)){
            return NameEditViewModel(dao, nameId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}