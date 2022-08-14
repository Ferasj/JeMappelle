package com.feras.Asami.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.feras.Asami.R
import com.feras.Asami.models.Tag

class TagItemAdapter(private val clickListener: (tagId : Long) -> Unit) : RecyclerView.Adapter<TagItemAdapter.TagItemViewHolder>() {

    var data  = listOf<Tag>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    class TagItemViewHolder(private val rootView: CardView) : RecyclerView.ViewHolder(rootView){
        val tagText = rootView.findViewById<TextView>(R.id.tag_name_text)

        companion object {
            fun inflateFrom(parent: ViewGroup) : TagItemViewHolder{
                val layoutInflater  = LayoutInflater.from(parent.context)
                val view = layoutInflater
                    .inflate(R.layout.item_tag, parent ,false) as CardView

                return TagItemViewHolder(view)
            }
        }

        fun bind(tagItem : Tag, clickListener: (tagId: Long) -> Unit){
            rootView.setOnClickListener {
                clickListener(tagItem.tagId)
            }
            tagText.text = tagItem.tagName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagItemViewHolder {
        return TagItemViewHolder.inflateFrom(parent)
    }

    override fun onBindViewHolder(holder: TagItemViewHolder, position: Int) {
        val tagItem = data[position]
        holder.bind(tagItem, clickListener)
    }

    override fun getItemCount(): Int {
        return data.size
    }


}