package com.feras.Asami.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.feras.Asami.database.NamesDatabase
import com.feras.Asami.databinding.FragmentTagEditBinding
import com.feras.Asami.models.Tag
import com.feras.Asami.viewmodelfactories.TagEditViewModelFactory
import com.feras.Asami.viewmodels.TagEditViewModel


class TagEditFragment : Fragment() {
    private var _binding: FragmentTagEditBinding? = null
    private val binding get() = _binding!!

    private var tag = Tag()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTagEditBinding.inflate(inflater, container, false)
        val view = binding.root

        val tagId = TagEditFragmentArgs.fromBundle(requireArguments()).tagId
        val application = requireNotNull(this.activity).application
        val dao = NamesDatabase.getInstance(application).namesDao
        val viewModelFactory = TagEditViewModelFactory(dao, tagId)
        val viewModel = ViewModelProvider(this, viewModelFactory).get(TagEditViewModel::class.java)


        viewModel.tag.observe(viewLifecycleOwner, Observer {
            tag = it
            binding.enterName.setText(it.tagName)
        })

        binding.toolbarCancleButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.saveNameButton.setOnClickListener {
            tag.tagName = binding.enterName.text.toString()
            viewModel.updateTag(tag)
            findNavController().popBackStack()
        }



        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}