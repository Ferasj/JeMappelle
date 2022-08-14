package com.feras.Asami.viewmodelfactories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.feras.Asami.database.NamesDao
import com.feras.Asami.viewmodels.NamesListViewModel
import java.lang.IllegalArgumentException

class NamesListViewModelFactory(private val dao: NamesDao, private val sortBy: String) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NamesListViewModel::class.java)) {
            return NamesListViewModel(dao, sortBy) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}