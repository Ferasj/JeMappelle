package com.feras.Asami.viewmodelfactories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.feras.Asami.database.NamesDao
import com.feras.Asami.viewmodels.TagListViewModel
import java.lang.IllegalArgumentException

class TagListViewModelFactory(private val dao: NamesDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TagListViewModel::class.java)){
            return TagListViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}