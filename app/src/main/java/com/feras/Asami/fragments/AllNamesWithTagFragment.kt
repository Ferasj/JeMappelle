package com.feras.Asami.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.feras.Asami.adapters.AllNamesItemAdapter
import com.feras.Asami.database.NamesDatabase
import com.feras.Asami.databinding.FragmentAllNamesWithTagBinding
import com.feras.Asami.viewmodelfactories.AllNamesWithTagViewModelFactory
import com.feras.Asami.viewmodels.AllNamesWithTagViewModel


class AllNamesWithTagFragment : Fragment() {
    private var _binding: FragmentAllNamesWithTagBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAllNamesWithTagBinding.inflate(inflater, container, false)
        val view = binding.root

        val tagId = AllNamesWithTagFragmentArgs.fromBundle(requireArguments()).tagId

        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayShowTitleEnabled(false)
        binding.toolbarBackButton.setOnClickListener {
            findNavController().popBackStack()
        }

        val application = requireNotNull(this.activity).application
        val dao = NamesDatabase.getInstance(application).namesDao
        val viewModelFactory = AllNamesWithTagViewModelFactory(dao, tagId)
        val viewModel =
            ViewModelProvider(this, viewModelFactory).get(AllNamesWithTagViewModel::class.java)

        viewModel.currentTagName.observe(viewLifecycleOwner, Observer {
            binding.toolbarTitle.text = it
        })

        val adapter = AllNamesItemAdapter{
            val action = AllNamesWithTagFragmentDirections.actionAllNamesWithTagFragmentToNameDetailsFragment(it)
            findNavController().navigate(action)
        }
        binding.fragmentAllNamesWtihTagRecView.adapter = adapter


        viewModel.getAllNames().observe(viewLifecycleOwner, Observer {
            viewModel.getNamesWithClickedTag(tagId).observe(viewLifecycleOwner, Observer {
                adapter.data = it
            })
        })


        binding.toolbarTagEditButton.setOnClickListener {
            val action = AllNamesWithTagFragmentDirections.actionAllNamesWithTagFragmentToTagEditFragment(tagId)
            findNavController().navigate(action)
        }


        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}