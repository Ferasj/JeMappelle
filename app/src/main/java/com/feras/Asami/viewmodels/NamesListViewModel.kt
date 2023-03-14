package com.feras.Asami.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.feras.Asami.database.NamesDao
import com.feras.Asami.models.*
import kotlinx.coroutines.launch

class NamesListViewModel(private val dao: NamesDao, sortBy: String) : ViewModel() {

    lateinit var namesWithTags: LiveData<List<NameWithTags>>
    lateinit var names: LiveData<List<Name>>
    lateinit var tags: LiveData<List<Tag>>
    lateinit var relationships: LiveData<List<TagInName>>


    //test
    val test = MutableLiveData<List<NameWithTags>>()

    init {
        viewModelScope.launch {
            getOnStart(sortBy)
            names = dao.getAllNames()
            tags = dao.getAllTags()
            relationships = dao.getAllRelationships()


        }
    }

    fun deleteNoNameNames(listOfNameWithTags: List<NameWithTags>) {
        viewModelScope.launch {
            for (nameWithTags in listOfNameWithTags) {
                if (nameWithTags.name.name == "") {
                    for (tag in nameWithTags.listOfTag) {
                        dao.deleteTagFromTagInNameWithId(tag.tagId)
                    }
                    dao.removeName(nameWithTags.name)
                }
            }
        }

    }

    fun insertName(name: Name) {
        viewModelScope.launch {
            dao.insertName(name)
        }
    }

     private fun getOnStart(sortBy: String) {

        when (sortBy) {
            "name_asc" -> namesWithTags = dao.getNamesWithTagsNameASC()
            "name_des" -> namesWithTags = dao.getNamesWithTagsNameDES()
            "date_asc" -> namesWithTags = dao.getNamesWithTagsDateAddedASC()
            "date_des" -> namesWithTags = dao.getNamesWithTagsDateAddedDES()
            else -> namesWithTags = dao.getNamesWithTags()
        }

    }


    fun getAllSortedBy(sortBy: String) : LiveData<List<NameWithTags>> {
        var returnList : LiveData<List<NameWithTags>> = MutableLiveData<List<NameWithTags>>(null)
        viewModelScope.launch {
            when (sortBy) {
                "name_asc" -> returnList = dao.getNamesWithTagsNameASC()
                "name_des" -> returnList = dao.getNamesWithTagsNameDES()
                "date_asc" -> returnList = dao.getNamesWithTagsDateAddedASC()
                "date_des" -> returnList = dao.getNamesWithTagsDateAddedDES()
                else -> returnList = dao.getNamesWithTags()
            }

        }
        return returnList
    }

    fun searchNamesByKeyword(keyword: String) : LiveData<List<NameWithTags>>{
        return dao.searchNamesByKeyword(keyword)
    }

    //fun searchName(name: String) : LiveData<List<NameWithTags>>{
    //    return dao.getNamesByName(name)
    //}

    fun searchNameWithTag(tagName : String) : LiveData<List<NameWithTags>>{
        return dao.getNameWithTagsByTagName(tagName)
    }

    fun format(first : List<NameWithTags>, second : List<NameWithTags>) : LiveData<List<NameWithTags>>{
        var listToReturn = listOf<NameWithTags>()
        viewModelScope.launch {
            var data = first
            data = data.union(second).toList()
            listToReturn = data

        }
        return MutableLiveData(listToReturn)
    }


    fun checkForStart(list : List<NameWithTags>, keyWord : String): LiveData<List<NameWithTags>>{
        val listToReturn = mutableListOf<NameWithTags>()
        var wordsList = listOf<String>()
        viewModelScope.launch {
            for(nameWithTags in list){
                wordsList= nameWithTags.name.notes.split(" ").toList()
                for (word in wordsList){
                    if (word.lowercase().startsWith(keyWord.lowercase())){
                        listToReturn.add(nameWithTags)
                    }
                }
            }
        }
        return MutableLiveData(listToReturn)
    }

}