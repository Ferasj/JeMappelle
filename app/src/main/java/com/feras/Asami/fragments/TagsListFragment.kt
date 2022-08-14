package com.feras.Asami.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.feras.Asami.adapters.TagListItemAdapter
import com.feras.Asami.database.NamesDatabase
import com.feras.Asami.databinding.FragmentTagsListBinding
import com.feras.Asami.models.Tag
import com.feras.Asami.viewmodelfactories.TagListViewModelFactory
import com.feras.Asami.viewmodels.TagListViewModel


class TagsListFragment : Fragment() {

    private var _binding: FragmentTagsListBinding? = null
    private val binding get() = _binding!!
    private var listOfTags = listOf<Tag>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTagsListBinding.inflate(inflater, container, false)
        val view = binding.root

        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        binding.toolbarTitle.text = "Tags"
        (activity as AppCompatActivity).supportActionBar!!.setDisplayShowTitleEnabled(false)

        val application = requireNotNull(this.activity).application
        val dao = NamesDatabase.getInstance(application).namesDao
        val viewModelFactory = TagListViewModelFactory(dao)
        val viewModel = ViewModelProvider(this, viewModelFactory).get(TagListViewModel::class.java)

        val adapter = TagListItemAdapter{tagId, shouldDelete ->
            if (!shouldDelete){
                val action = TagsListFragmentDirections.actionTagsListFragmentToAllNamesWithTagFragment(tagId)
                findNavController().navigate(action)
            }else{
                val alert = AlertDialog.Builder(context!!)
                alert.setMessage("Are you sure you want to delete?")
                alert.setPositiveButton("Yes", { dialog, which ->
                    viewModel.deleteTag(tagId)
                })
                alert.setNegativeButton("No", { dialog, which ->
                })
                alert.show()

            }

        }


        binding.tagsRecView.adapter = adapter

        viewModel.listOfTags.observe(viewLifecycleOwner, Observer {
            if (it != null){
                adapter.data = it
                listOfTags = it
            }

        })


        binding.searchBar.doOnTextChanged { text, _, _, _ ->
            if(text !=null) {
                viewModel.searchFor(text.toString()).observe(viewLifecycleOwner, Observer {
                    adapter.data = it
                })
            }
        }

        return view
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}