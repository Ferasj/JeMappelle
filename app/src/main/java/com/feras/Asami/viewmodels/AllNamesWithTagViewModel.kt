package com.feras.Asami.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.feras.Asami.database.NamesDao
import com.feras.Asami.models.NameWithTags
import kotlinx.coroutines.launch

class AllNamesWithTagViewModel(private val dao: NamesDao, private val tagId : Long) : ViewModel() {

    lateinit var currentTagName : LiveData<String>



    init {
        viewModelScope.launch {
            currentTagName = dao.getTagNameById(tagId)
        }
    }

    fun getAllNames(): LiveData<List<NameWithTags>>{
        return dao.getNamesWithTags()
    }


    fun getNamesWithClickedTag(tagId : Long) : LiveData<List<NameWithTags>>{
        return dao.getNameWithTagsByTagId(tagId)
    }


//    Old function, reuse if needed!
//    fun getNamesWithClickedTag(tagId : Long, listOfNameWithTags  : List<NameWithTags>) : LiveData<List<NameWithTags>>{
//        val listToReturn = mutableListOf<NameWithTags>()
//        viewModelScope.launch {
//            for(index in 0 until listOfNameWithTags.size){
//                val listOfTagId = mutableListOf<Long>()
//                for (tag in listOfNameWithTags[index].listOfTag){
//                    listOfTagId.add(tag.tagId)
//                }
//                if (tagId in listOfTagId){
//                    listToReturn.add(listOfNameWithTags[index])
//                }
//            }
//        }
//        return MutableLiveData(listToReturn)
//    }
}