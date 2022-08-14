package com.feras.Asami.viewmodelfactories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.feras.Asami.database.NamesDao
import com.feras.Asami.viewmodels.TagEditViewModel
import java.lang.IllegalArgumentException

class TagEditViewModelFactory(private val dao : NamesDao, private val tagId : Long) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TagEditViewModel::class.java)){
            return TagEditViewModel(dao, tagId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}