package com.feras.Asami.fragments

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.feras.Asami.MainActivity
import com.feras.Asami.R
import com.feras.Asami.adapters.NameItemAdapter
import com.feras.Asami.database.NamesDatabase
import com.feras.Asami.databinding.FragmentNamesListBinding
import com.feras.Asami.models.Name
import com.feras.Asami.models.NameWithTags
import com.feras.Asami.models.Tag
import com.feras.Asami.models.TagInName
import com.feras.Asami.util.generateFile
import com.feras.Asami.util.goToFileIntent
import com.feras.Asami.viewmodelfactories.NamesListViewModelFactory
import com.feras.Asami.viewmodels.NamesListViewModel
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import com.google.android.material.snackbar.Snackbar
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

//added by Bing
import androidx.recyclerview.widget.RecyclerView



class NamesListFragment : Fragment() {
    private var _binding: FragmentNamesListBinding? = null
    private val binding get() = _binding!!

    private var sortBy = MutableLiveData<String>("name_asc")

    private var listOfNamesWithTags = listOf<NameWithTags>()
    private var listOfTags = listOf<Tag>()
    private var listOfRelationships = listOf<TagInName>()
    private var listOfNames = listOf<Name>()

    private var fileType = ".csv"
    private var openFileAfterExport = false

    lateinit var database: NamesDatabase

    private val SHARED_PREFS = "sharedPrefs"
    private val SORT_KEY = "sortBy"
    private val REC_POSITION_KEY = "recPosition"

    private var positionOnRec = 0
    private var mScrollY = 0



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNamesListBinding.inflate(inflater, container, false)
        val view = binding.root

        loadData()

        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        binding.toolbarTitle.text = "Names"
        (activity as AppCompatActivity).supportActionBar!!.setDisplayShowTitleEnabled(false)
        setHasOptionsMenu(true)


        val application = requireNotNull(this.activity).application
        database = NamesDatabase.getInstance(application)
        val namesDao = database.namesDao
        val viewModelFactory = NamesListViewModelFactory(namesDao, sortBy.value!!)
        val viewModel =
            ViewModelProvider(this, viewModelFactory).get(NamesListViewModel::class.java)

        binding.fab.setOnClickListener {
            val name = Name()
            name.dateAdded = getTodayDate()
            name.dateModified = getTodayDate()
            viewModel.insertName(name)
            resetRecPosition()
            findNavController().navigate(R.id.action_namesListFragment_to_addNewNameFragment)
        }

        val adapter = NameItemAdapter { nameId, tagId ->
            if (tagId.toString() == "0") {
                val action = NamesListFragmentDirections
                    .actionNamesListFragmentToNameDetailsFragment(nameId)
                findNavController().navigate(action)
            } else {
                val action =
                    NamesListFragmentDirections
                        .actionNamesListFragmentToAllNamesWithTagFragment(tagId)
                findNavController().navigate(action)
            }

        }

        binding.namesRecView.adapter = adapter

        //this part is responsible for the sort functionality
        sortBy.observe(viewLifecycleOwner, Observer { sortKey ->
            viewModel.getAllSortedBy(sortKey).observe(viewLifecycleOwner, Observer {
                if (binding.searchBar.text.toString() == "")
                    adapter.data = it
                binding.namesRecView.scrollToPosition(positionOnRec)
            })
        })

        //this part is responsible for the search functionality
        binding.searchBar.doOnTextChanged { text, start, before, count ->
            if (text != null && text.isNotEmpty()) {
                viewModel.searchNamesByKeyword(text.toString())
                    .observe(viewLifecycleOwner, Observer { results ->
                        adapter.data = results
                        binding.namesRecView.scrollToPosition(positionOnRec)
                    })
            }
                else {
                    viewModel.getAllSortedBy(sortBy.value!!)
                        .observe(viewLifecycleOwner, Observer {
                            adapter.data = it
                            binding.namesRecView.scrollToPosition(positionOnRec)
                        })
                }
            }


/*
        binding.searchBar.doOnTextChanged { text, start, before, count ->
            if (text != null) {
                if (text != "") {
                    if (text.length > 0) {
                        viewModel.searchName(text.toString())
                            .observe(viewLifecycleOwner, Observer { first ->
                                viewModel.searchNameWithTag(text.toString())
                                    .observe(viewLifecycleOwner, Observer { second ->
                                        viewModel.format(first, second)
                                            .observe(viewLifecycleOwner, Observer {third->
                                                viewModel.checkForStart(third, text.toString())
                                                    .observe(viewLifecycleOwner, Observer {

                                                        val listOne : MutableList<NameWithTags> = it as MutableList<NameWithTags>
                                                        val listTwo : MutableList<NameWithTags> = first as MutableList<NameWithTags>

                                                        val listToShow = listOne
                                                        listToShow.addAll(listTwo)

                                                        adapter.data = listToShow
                                                        binding.namesRecView.scrollToPosition(positionOnRec)

                                                    })

                                            })
                                    })

                            })
                    }



                    if (binding.searchBar.text.toString().isEmpty()) {
                        viewModel.getAllSortedBy(sortBy.value!!)
                            .observe(viewLifecycleOwner, Observer {
                                adapter.data = it
                                binding.namesRecView.scrollToPosition(positionOnRec)
                            })
                    }
                }

            }
        }
*/

        viewModel.namesWithTags.observe(viewLifecycleOwner, Observer {
            listOfNamesWithTags = it
            viewModel.deleteNoNameNames(listOfNamesWithTags)
        })

        viewModel.names.observe(viewLifecycleOwner, Observer {
            listOfNames = it
        })

        viewModel.tags.observe(viewLifecycleOwner, Observer {
            listOfTags = it
        })

        viewModel.relationships.observe(viewLifecycleOwner, Observer {
            listOfRelationships = it
        })

        binding.namesRecView.scrollToPosition(positionOnRec)

        binding.namesRecView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val sharedPreferences = activity!!.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                val currentScrollPosition = sharedPreferences.getInt(REC_POSITION_KEY, 0) + dy
                editor.putInt(REC_POSITION_KEY, currentScrollPosition)
                editor.apply()
            }
        })


        return view // create the view hierarchy associated with the fragment.
    }












/*
    override fun onPause() {
        saveRecPosition()
        super.onPause()
    }

    fun saveRecPosition(){
        val sharedPreferences =
            this.activity!!.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val position =
            (binding.namesRecView.getLayoutManager() as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()

        editor.putInt(REC_POSITION_KEY, position)
        editor.apply()
    }
*/

    fun resetRecPosition(){
        val sharedPreferences =
            this.requireActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val position =
            (binding.namesRecView.getLayoutManager() as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()

        editor.putInt(REC_POSITION_KEY, 0)
        editor.apply()
    }



// The code below is responsbile for exporting the names to a CSV file. the functions used have been deprecated and are no longer in use
    // I don't think the code is correct anyway, as there is no value in exporting names alone or tags alone. I need the full database
    // I'll gray it for now as I can copy the whole database with me when I move from phone to another. I might come later to fix it


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_toolbar, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val permissionGranted = (activity as MainActivity).hasPermission()
        if (!permissionGranted && (item.itemId == R.id.export_names_csv
                    || item.itemId == R.id.export_tags_csv
                    || item.itemId == R.id.export_names_with_tags_csv
                    || item.itemId == R.id.export_relationship_to_csv)
        ) {
            (activity as MainActivity).requestStoragePermission()
        } else {
            when (item.itemId) {
                R.id.export_names_csv -> if (listOfNames.isNotEmpty()) exportDatabaseToCSVFile(1) else showSnackBar(
                    "Selected table is empty!"
                )
                R.id.export_tags_csv -> if (listOfTags.isNotEmpty()) exportDatabaseToCSVFile(2) else showSnackBar(
                    "Selected table is empty!"
                )
                R.id.export_names_with_tags_csv -> if (listOfNamesWithTags.isNotEmpty()) exportDatabaseToCSVFile(
                    3
                ) else showSnackBar("Selected table is empty!")
                R.id.export_relationship_to_csv -> if (listOfRelationships.isNotEmpty()) exportDatabaseToCSVFile(
                    4
                ) else showSnackBar("Selected table is empty!")
                R.id.sort_name_asc -> {
                    sortBy.value = "name_asc"
                    saveData()
                }
                R.id.sort_name_des -> {
                    sortBy.value = "name_des"
                    saveData()
                }
                R.id.sort_date_added_asc -> {
                    sortBy.value = "date_asc"
                    saveData()
                }
                R.id.sort_date_added_des -> {
                    sortBy.value = "date_des"
                    saveData()
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }


    private fun exportDatabaseToCSVFile(actionId: Int) {
        var fileName = "name" + fileType
        if (actionId == 1) {
            fileName = "NamesCSV" + fileType
        } else if (actionId == 2) {
            fileName = "TagsCSV" + fileType
        } else if (actionId == 3) {
            fileName = "NamesWithTagsCSV" + fileType
        } else if (actionId == 4) {
            fileName = "RelationshipCSV" + fileType
        }
        val csvFile = generateFile(requireContext(), fileName)
        if (csvFile != null) {
            exportNamesToCSVFile(csvFile, actionId)
            val intent = goToFileIntent(requireContext(), csvFile)
            try {
                if (openFileAfterExport) {
                    startActivity(intent)
                    Toast.makeText(context, "File saved to Downloads!", Toast.LENGTH_SHORT).show()
                } else {
                    showSnackBar("File saved to Downloads!")
                }

            } catch (e: Exception) {
                showSnackBar("Please download a .CSV reader!")
            }

        }
    }

    private fun exportNamesToCSVFile(csvFile: File, actionId: Int) {
        try {


            csvWriter().open(csvFile, append = false) {
                if (actionId == 1) {
                    writeRow(
                        listOf(
                            "[nameId]",
                            "[name]",
                            "[notes]",
                            "[date_added]",
                            "[date_modified]"
                        )
                    )
                    listOfNames.forEachIndexed { index, name ->
                        writeRow(
                            listOf(
                                name.nameId,
                                name.name,
                                name.notes,
                                name.dateAdded,
                                name.dateModified
                            )
                        )
                    }
                } else if (actionId == 2) {
                    writeRow(listOf("[tagId]", "[tagName]"))
                    listOfTags.forEachIndexed { index, tag ->
                        writeRow(listOf(tag.tagId, tag.tagName))
                    }

                } else if (actionId == 3) {
                    writeRow(
                        listOf(
                            "[nameId]",
                            "[name]",
                            "[notes]",
                            "[date_added]",
                            "[date_modified]",
                            "[tagList]"
                        )
                    )
                    listOfNamesWithTags.forEachIndexed { index, nameWithTags ->
                        var stringOfTagNames = "("
                        nameWithTags.listOfTag.forEachIndexed { index, tag ->
                            if (index == 0) {
                                stringOfTagNames += "${tag.tagName}"
                            } else {
                                stringOfTagNames += ",${tag.tagName}"
                            }
                        }
                        stringOfTagNames += ")"
                        writeRow(
                            listOf(
                                nameWithTags.name.nameId,
                                nameWithTags.name.name,
                                nameWithTags.name.notes,
                                nameWithTags.name.dateAdded,
                                nameWithTags.name.dateModified,
                                stringOfTagNames
                            )
                        )
                    }
                } else if (actionId == 4) {
                    writeRow(listOf("[nameId]", "[tagId]"))
                    listOfRelationships.forEachIndexed { index, tagInName ->
                        writeRow(listOf(tagInName.nameId, tagInName.tagId))
                    }
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    fun showSnackBar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(ContextCompat.getColor(this.requireContext(), R.color.blue_dark_700))
            .show()
    }

    fun getTodayDate(): String {
        val sdf = SimpleDateFormat("dd.MM.yyyy.")
        val calendar = Calendar.getInstance()
        val date = sdf.format(calendar.time)
        return date
    }

    private fun saveData() {
        val sharedPreferences =
            this.requireActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString(SORT_KEY, sortBy.value)
        editor.apply()
    }

    private fun loadData() {
        val sharedPreferences =
            this.requireActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        sortBy.postValue(sharedPreferences.getString(SORT_KEY, "name_asc"))
        positionOnRec = sharedPreferences.getInt(REC_POSITION_KEY, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        resetRecPosition()
        _binding = null
    }

}