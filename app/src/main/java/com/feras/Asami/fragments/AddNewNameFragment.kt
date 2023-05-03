package com.feras.Asami.fragments

import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.feras.Asami.R
import com.feras.Asami.database.NamesDatabase
import com.feras.Asami.databinding.FragmentAddNewNameBinding
import com.feras.Asami.models.Name
import com.feras.Asami.models.Tag
import com.feras.Asami.models.TagInName
import com.feras.Asami.viewmodelfactories.AddNewNameViewModelFactory
import com.feras.Asami.viewmodels.AddNewNameViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar


class AddNewNameFragment : Fragment() {
    private var _binding: FragmentAddNewNameBinding? = null
    private val binding get() = _binding!!

    private val listOfTagsOnScreen = mutableListOf<String>()

    private var listOfTagNamesInDB = listOf<String>()
    private var listOfTagIdInDB = listOf<Long>()

    private var currentName = Name()

    private var isReady = false

    private lateinit var viewModelOne: AddNewNameViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddNewNameBinding.inflate(inflater, container, false)
        val view = binding.root

        val application = requireNotNull(this.activity).application
        val namesDao = NamesDatabase.getInstance(application).namesDao
        val viewModelFactory = AddNewNameViewModelFactory(namesDao)
        val viewModel =
            ViewModelProvider(this, viewModelFactory).get(AddNewNameViewModel::class.java)

        viewModelOne = viewModel


        binding.toolbarCancleButton.setOnClickListener {
            viewModel.deleteFromTagInTableWithNameId(currentName.nameId)
            findNavController().popBackStack()
        }

        viewModel.currentName.observe(viewLifecycleOwner, Observer {
            it.let {
                if (it !=null)
                    currentName = it
            }
        })

        tagEntryChip(viewModel)


        viewModel.listOfTagNames.observe(viewLifecycleOwner, Observer {
            listOfTagNamesInDB = it

            val arrayAdapter = ArrayAdapter<String>(view.context, android.R.layout.simple_list_item_1, it)
            binding.enterTags.setAdapter(arrayAdapter)

        })

        viewModel.listOfTagId.observe(viewLifecycleOwner, Observer {
            listOfTagIdInDB = it
            if (!it.isEmpty() && isReady) {
                val tagInName = TagInName()
                tagInName.nameId = currentName.nameId
                tagInName.tagId = it.last()
                viewModel.addTagInName(tagInName)
                isReady = false
            }
        })


        binding.createNameButton.setOnClickListener {
            currentName.name = binding.enterName.text.toString()
            currentName.notes = binding.enterNotes.text.toString()
            if (binding.enterName.text.toString() != ""){
                viewModel.saveName(currentName)
                findNavController().navigate(R.id.action_addNewNameFragment_to_namesListFragment)
            }else{
                showSnackBar("Please enter a name!")
            }
            
        }


        return view
    }

    private fun tagEntryChip(viewModel: AddNewNameViewModel) {

        binding.enterTags.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {

                binding.apply {
                    val name = enterTags.text.toString()
                    if (name in listOfTagsOnScreen) {
                        showSnackBar("Tag already added!")
                        return@apply
                    }
                    if (name in listOfTagNamesInDB) {
                        createTagChips(name, viewModel)
                        enterTags.text.clear()
                        viewModel.reCallTagNames()
                        val indexOfTheName = listOfTagNamesInDB.indexOf(name).toString()
                        if (indexOfTheName.length > 0) {
                            val tagInName = TagInName()
                            tagInName.nameId = currentName.nameId
                            tagInName.tagId = listOfTagIdInDB[indexOfTheName.toInt()]
                            viewModel.addTagInName(tagInName)
                        }
                        isReady = true
                    } else {
                        createTagChips(name, viewModel)
                        enterTags.text.clear()
                        val tag = Tag()
                        tag.tagName = name
                        viewModel.insertTag(tag)
                        viewModel.reCallTagNames()
                        isReady = true
                    }

                }

                return@setOnKeyListener true
            }
            false
        }
    }

    private fun createTagChips(name: String, viewModel: AddNewNameViewModel) {
        val chip = Chip(context)
        chip.apply {
            text = name
            chipIcon = ContextCompat.getDrawable(
                context,
                R.drawable.ic_launcher_background,
            )
            isChipIconVisible = false
            isCloseIconVisible = true
            isClickable = false
            isCheckable = false
            binding.apply {
                tagChipsGroup.addView(chip as View)
                listOfTagsOnScreen.add(name)
                chip.setOnCloseIconClickListener {
                    tagChipsGroup.removeView(chip as View)
                    listOfTagsOnScreen.remove(name)
                    val tagInName = TagInName()
                    tagInName.nameId = currentName.nameId
                    val indexOfTagName = listOfTagNamesInDB.indexOf(name)
                    val tagId = listOfTagIdInDB[indexOfTagName]
                    tagInName.tagId = tagId
                    viewModel.removeTagInName(tagInName)
                }
            }
        }
    }

    fun showSnackBar(message : String){
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.blue_dark_700))
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }

}