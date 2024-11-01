package com.pycreation.e_commerce.admin.listening.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pycreation.e_commerce.R
import com.pycreation.e_commerce.databinding.TagsLyItemsBinding

class TagAdapter(
    private val tagList: MutableList<String>,
    private val onRemoveTag: (String) -> Unit
) : RecyclerView.Adapter<TagAdapter.TagHolder>() {

    inner class TagHolder(val binding: TagsLyItemsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagHolder {
        val binding = TagsLyItemsBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return TagHolder(binding)
    }

    override fun getItemCount(): Int {
        return tagList.size
    }

    override fun onBindViewHolder(holder: TagHolder, position: Int) {
        holder.binding.tag.text = tagList[position]
        holder.binding.deleteTag.setOnClickListener {
            if (position != RecyclerView.NO_POSITION) {
                val uriToRemove = tagList[position]
                onRemoveTag(uriToRemove) // Call the removal function
                notifyItemRemoved(position) // Notify adapter about the removal
            }
        }
    }
}
