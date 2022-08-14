package com.feras.Asami.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.feras.Asami.R
import com.feras.Asami.adapters.TagItemAdapter
import com.feras.Asami.database.NamesDatabase
import com.feras.Asami.databinding.FragmentNameDetailsBinding
import com.feras.Asami.viewmodelfactories.NameDetailsViewModelFactory
import com.feras.Asami.viewmodels.NameDetailsViewModel


class NameDetailsFragment : Fragment() {
    private var _binding : FragmentNameDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNameDetailsBinding.inflate(inflater, container, false)
        val view = binding.root

        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayShowTitleEnabled(false)
        
        binding.toolbarBackButton.setOnClickListener {
            findNavController().navigate(R.id.action_nameDetailsFragment_to_namesListFragment)
        }

        val nameId = NameDetailsFragmentArgs.fromBundle(requireArguments()).nameId

        val application = requireNotNull(this.activity).application
        val dao = NamesDatabase.getInstance(application).namesDao
        val viewModelFactory = NameDetailsViewModelFactory(dao, nameId)
        val viewModel = ViewModelProvider(this, viewModelFactory).get(NameDetailsViewModel::class.java)

        val adapter = TagItemAdapter{
            val action = NameDetailsFragmentDirections.actionNameDetailsFragmentToAllNamesWithTagFragment(it)
            findNavController().navigate(action)
        }
        binding.nameDetailsRecView.adapter = adapter

        binding.toolbarDeleteButton.setOnClickListener {_->
            val alert = AlertDialog.Builder(context!!)
            alert.setMessage("Are you sure you want to delete?")
            alert.setPositiveButton("Yes", { dialog, which ->
                viewModel.deleteName(nameId)
                findNavController().popBackStack()
            })
            alert.setNegativeButton("No", { dialog, which ->
            })
            alert.show()
        }

        binding.editNameButton.setOnClickListener {
            val action = NameDetailsFragmentDirections.actionNameDetailsFragmentToNameEditFragment(nameId)
            findNavController().navigate(action)
        }


        viewModel.currentNameDetails.observe(viewLifecycleOwner, Observer {
            binding.toolbarTitle.text = it.name.name
            binding.noteText.text = it.name.notes
            binding.dateAddedText.text = "Date added: ${it.name.dateAdded}"
            binding.dateModifiedText.text = "Date modified: ${it.name.dateModified}"
            adapter.data = it.listOfTag

        })


        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }




}