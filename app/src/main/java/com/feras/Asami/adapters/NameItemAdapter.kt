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

class NameItemAdapter(private val clickListener: (nameId: Long, tagId : Long) -> Unit) : RecyclerView.Adapter<NameItemAdapter.NameItemViewHolder>() {

    var data = listOf<NameWithTags>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class NameItemViewHolder(private val rootView: CardView) : RecyclerView.ViewHolder(rootView) {

        val nameText = rootView.findViewById<TextView>(R.id.item_name_text)
//        val dateAdded = rootView.findViewById<TextView>(R.id.date_added)
//        val dateModified = rootView.findViewById<TextView>(R.id.date_modified)
        val tagRecyclerView = rootView.findViewById<RecyclerView>(R.id.tag_recycler_view)

        companion object {
            fun inflateFrom(parent: ViewGroup): NameItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater
                    .inflate(R.layout.item_name, parent, false) as CardView
                return NameItemViewHolder(view)
            }
        }

        fun bind(item: Name, clickListener: (nameId: Long, tagId: Long) -> Unit) {
            rootView.setOnClickListener {
                clickListener(item.nameId, 0)
            }
            nameText.text = item.name
//            dateAdded.text = "Date added ${item.dateAdded}"
//            dateModified.text = "Date modified ${item.dateModified}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NameItemViewHolder {
        return NameItemViewHolder.inflateFrom(parent)
    }

    override fun onBindViewHolder(holder: NameItemViewHolder, position: Int) {
        val item = data[position].name
        holder.bind(item, clickListener)
        setTagRecycler(holder.tagRecyclerView, data[position].listOfTag)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    private fun setTagRecycler(recyclerView: RecyclerView, tagList : List<Tag>){
        val adapter = TagItemAdapter{
            clickListener(0, it)
        }
        recyclerView.adapter = adapter
        adapter.data = tagList

    }


}