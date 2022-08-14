package com.feras.Asami.viewmodelfactories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.feras.Asami.database.NamesDao
import com.feras.Asami.viewmodels.AddNewNameViewModel
import java.lang.IllegalArgumentException

class AddNewNameViewModelFactory(private val dao: NamesDao): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddNewNameViewModel::class.java)){
            return AddNewNameViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}