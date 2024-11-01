package com.pycreation.e_commerce.admin.listening.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.pycreation.e_commerce.R

class ImageAdapter(
    private val imageUris: MutableList<Uri>,
    private val onImageRemove: (Uri) -> Unit,
    private val onAddImageClick: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_IMAGE = 1
        const val VIEW_TYPE_ADD_BUTTON = 2
    }

    inner class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ShapeableImageView = view.findViewById(R.id.productImgItem)
        val deleteButton: ImageView = view.findViewById(R.id.deleteItem)
        init {
            deleteButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val uriToRemove = imageUris[position]
                    onImageRemove(uriToRemove) // Call the removal function
                    notifyItemRemoved(position) // Notify adapter about the removal
                }
            }
        }
    }

    inner class AddImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val addButton: CardView = view.findViewById(R.id.addProductListeningPicture)

        init {
            addButton.setOnClickListener {
                onAddImageClick() // Call the add image function
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < imageUris.size) VIEW_TYPE_IMAGE else VIEW_TYPE_ADD_BUTTON
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_IMAGE) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.product_listening_pic_item, parent, false)
            ImageViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.add_img_product_listening_item, parent, false)
            AddImageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ImageViewHolder) {
            val uri = imageUris[position]
            holder.imageView.setImageURI(uri)
        } else if (holder is AddImageViewHolder) {
            holder.addButton.setOnClickListener {
                onAddImageClick() // Call the function to handle adding images
            }
        }
    }

    override fun getItemCount(): Int = imageUris.size + 1 // +1 for the Add Image button
}
