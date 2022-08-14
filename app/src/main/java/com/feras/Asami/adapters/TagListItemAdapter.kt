package com.feras.Asami.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.feras.Asami.R
import com.feras.Asami.models.Tag

class TagListItemAdapter(private val clickListener: (tagId : Long, shouldDelete : Boolean) -> Unit) : RecyclerView.Adapter<TagListItemAdapter.TagItemViewHolder>(){

    var data  = listOf<Tag>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    class TagItemViewHolder(private val rootView: CardView) : RecyclerView.ViewHolder(rootView){
        val tagText = rootView.findViewById<TextView>(R.id.tag_item_name_text)
        val deleteButton = rootView.findViewById<ImageButton>(R.id.tag_delete_button)

        companion object {
            fun inflateFrom(parent: ViewGroup) : TagItemViewHolder{
                val layoutInflater  = LayoutInflater.from(parent.context)
                val view = layoutInflater
                    .inflate(R.layout.item_tag_parent, parent ,false) as CardView

                return TagItemViewHolder(view)
            }
        }

        fun bind(tagItem : Tag, clickListener: (tagId: Long, shouldDelete : Boolean) -> Unit){
            rootView.setOnClickListener {
                clickListener(tagItem.tagId, false)
            }
            deleteButton.setOnClickListener {
                clickListener(tagItem.tagId, true)
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