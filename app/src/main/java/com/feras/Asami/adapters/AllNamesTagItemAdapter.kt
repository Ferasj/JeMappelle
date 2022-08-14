package com.feras.Asami.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.feras.Asami.R
import com.feras.Asami.models.Tag

class AllNamesTagItemAdapter : RecyclerView.Adapter<AllNamesTagItemAdapter.AllNamesTagItemViewHolder>(){


    var data  = listOf<Tag>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    class AllNamesTagItemViewHolder(rootView: CardView) : RecyclerView.ViewHolder(rootView){
        val tagText = rootView.findViewById<TextView>(R.id.tag_name_text)

        companion object {
            fun inflateFrom(parent: ViewGroup) : AllNamesTagItemViewHolder{
                val layoutInflater  = LayoutInflater.from(parent.context)
                val view = layoutInflater
                    .inflate(R.layout.item_tag, parent ,false) as CardView

                return AllNamesTagItemViewHolder(view)
            }
        }

        fun bind(tagItem : Tag){
            tagText.text = tagItem.tagName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllNamesTagItemViewHolder {
        return AllNamesTagItemViewHolder.inflateFrom(parent)
    }

    override fun onBindViewHolder(holder: AllNamesTagItemViewHolder, position: Int) {
        val tagItem = data[position]
        holder.bind(tagItem)
    }

    override fun getItemCount(): Int {
        return data.size
    }
}