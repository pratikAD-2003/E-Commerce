package com.pycreation.e_commerce.consumer.dashboard.adaptors

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pycreation.e_commerce.R

class HomeImageSliderAdapter(private val imageList: List<Any>) : RecyclerView.Adapter<HomeImageSliderAdapter.ImageViewHolder>() {

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.bannerImgItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.home_screen_banner_img_ly, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val realPosition = when (position) {
            0 -> imageList.size - 1 // The fake first position should show the last image
            itemCount - 1 -> 0 // The fake last position should show the first image
            else -> position - 1 // Adjust for the "fake" items
        }

        val imageUrl = imageList[realPosition]
        Glide.with(holder.itemView.context)
            .load(imageUrl)  // Use Glide to load the image from URL
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return imageList.size + 2
    }
}
