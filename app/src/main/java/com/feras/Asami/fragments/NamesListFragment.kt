package com.feras.Asami.fragments

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
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

        sortBy.observe(viewLifecycleOwner, Observer { sortKey ->
            viewModel.getAllSortedBy(sortKey).observe(viewLifecycleOwner, Observer {
                if (binding.searchBar.text.toString() == "")
                    adapter.data = it
            })
        })


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
                                                        adapter.data = it

                                                    })

                                            })
                                    })

                            })
                    }
                    if (binding.searchBar.text.toString().isEmpty()) {
                        viewModel.getAllSortedBy(sortBy.value!!)
                            .observe(viewLifecycleOwner, Observer {
                                adapter.data = it
                            })
                    }
                }

            }
        }


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


        return view
    }


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
        val csvFile = generateFile(context!!, fileName)
        if (csvFile != null) {
            exportNamesToCSVFile(csvFile, actionId)
            val intent = goToFileIntent(context!!, csvFile)
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
        Snackbar.make(view!!, message, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(ContextCompat.getColor(this.context!!, R.color.blue_dark_700))
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
            this.activity!!.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString(SORT_KEY, sortBy.value)
        editor.apply()
    }

    private fun loadData() {
        val sharedPreferences =
            this.activity!!.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        sortBy.postValue(sharedPreferences.getString(SORT_KEY, "name_asc"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}