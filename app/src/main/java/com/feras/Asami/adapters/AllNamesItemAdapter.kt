package com.feras.Asami.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.feras.Asami.R
import com.feras.Asami.models.Name
import com.feras.Asami.models.NameWithTags
import com.feras.Asami.models.Tag

class AllNamesItemAdapter(private val clickListener: (nameId : Long) -> Unit): RecyclerView.Adapter<AllNamesItemAdapter.AllNamesItemViewHolder>() {

    var data = listOf<NameWithTags>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class AllNamesItemViewHolder(private val rootView: CardView) : RecyclerView.ViewHolder(rootView) {

        val nameText = rootView.findViewById<TextView>(R.id.all_names_name_text)
        val tagRecyclerView = rootView.findViewById<RecyclerView>(R.id.all_names_tag_rec_view)
//        val dateAdded = rootView.findViewById<TextView>(R.id.date_added)
//        val dateModified = rootView.findViewById<TextView>(R.id.date_modified)

        companion object {
            fun inflateFrom(parent: ViewGroup): AllNamesItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater
                    .inflate(R.layout.item_all_names_with_tag, parent, false) as CardView
                return AllNamesItemViewHolder(view)
            }
        }

        fun bind(item: Name, clickListener: (nameId: Long) -> Unit){
            rootView.setOnClickListener {
                clickListener(item.nameId)
            }
            nameText.text = item.name
//            dateAdded.text = "Date added ${item.dateAdded}"
//            dateModified.text = "Date modified ${item.dateModified}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllNamesItemViewHolder {
        return AllNamesItemViewHolder.inflateFrom(parent)
    }

    override fun onBindViewHolder(holder: AllNamesItemViewHolder, position: Int) {
        val item = data[position].name
        holder.bind(item, clickListener)
        setTagRecycler(holder.tagRecyclerView, data[position].listOfTag)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    private fun setTagRecycler(recyclerView: RecyclerView, tagList : List<Tag>){
        val adapter = AllNamesTagItemAdapter()
        recyclerView.adapter = adapter
        adapter.data = tagList

    }
}