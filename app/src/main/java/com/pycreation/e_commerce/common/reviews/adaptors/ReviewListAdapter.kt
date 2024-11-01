package com.pycreation.e_commerce.common.reviews.adaptors

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.pycreation.e_commerce.R
import com.pycreation.e_commerce.common.reviews.models.Review
import com.pycreation.e_commerce.databinding.ReviwedProductsLyBinding
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class ReviewListAdapter(private val context: Context, private val list: List<Review>) :
    Adapter<ReviewListAdapter.ReviewListHolder>() {
    public inner class ReviewListHolder(val binding: ReviwedProductsLyBinding) :
        ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewListHolder {
        val binding = ReviwedProductsLyBinding.inflate(LayoutInflater.from(context), parent, false)
        return ReviewListHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ReviewListHolder, position: Int) {
        val current = list[position]
        Glide.with(context).load(current.productDetails.productImages[0])
            .into(holder.binding.reviewImgItem)
        holder.binding.reviewProductNameItem.text = current.productDetails.productName
        holder.binding.reviewTextItem.text = current.reviewText
        holder.binding.reviewDateItem.text = convertToDateMonthYear(current.timestamp)
        setupAlreadyRating(current.rating, holder)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertToDateMonthYear(input: String): String {
        val zonedDateTime = ZonedDateTime.parse(input)

        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")

        return zonedDateTime.format(formatter)
    }

    private fun setupAlreadyRating(rating: Int, holder: ReviewListHolder) {
        when (rating) {
            1 -> {
                holder.binding.star1ReviewProductItemLy.setImageResource(R.drawable.full_star)
                holder.binding.star2ReviewProductItemLy.setImageResource(R.drawable.blank_star)
                holder.binding.star3ReviewProductItemLy.setImageResource(R.drawable.blank_star)
                holder.binding.star4ReviewProductItemLy.setImageResource(R.drawable.blank_star)
                holder.binding.star5ReviewProductItemLy.setImageResource(R.drawable.blank_star)
            }

            2 -> {
                holder.binding.star1ReviewProductItemLy.setImageResource(R.drawable.full_star)
                holder.binding.star2ReviewProductItemLy.setImageResource(R.drawable.full_star)
                holder.binding.star3ReviewProductItemLy.setImageResource(R.drawable.blank_star)
                holder.binding.star4ReviewProductItemLy.setImageResource(R.drawable.blank_star)
                holder.binding.star5ReviewProductItemLy.setImageResource(R.drawable.blank_star)
            }

            3 -> {
                holder.binding.star1ReviewProductItemLy.setImageResource(R.drawable.full_star)
                holder.binding.star2ReviewProductItemLy.setImageResource(R.drawable.full_star)
                holder.binding.star3ReviewProductItemLy.setImageResource(R.drawable.full_star)
                holder.binding.star4ReviewProductItemLy.setImageResource(R.drawable.blank_star)
                holder.binding.star5ReviewProductItemLy.setImageResource(R.drawable.blank_star)
            }

            4 -> {
                holder.binding.star1ReviewProductItemLy.setImageResource(R.drawable.full_star)
                holder.binding.star2ReviewProductItemLy.setImageResource(R.drawable.full_star)
                holder.binding.star3ReviewProductItemLy.setImageResource(R.drawable.full_star)
                holder.binding.star4ReviewProductItemLy.setImageResource(R.drawable.full_star)
                holder.binding.star5ReviewProductItemLy.setImageResource(R.drawable.blank_star)
            }

            5 -> {
                holder.binding.star1ReviewProductItemLy.setImageResource(R.drawable.full_star)
                holder.binding.star2ReviewProductItemLy.setImageResource(R.drawable.full_star)
                holder.binding.star3ReviewProductItemLy.setImageResource(R.drawable.full_star)
                holder.binding.star4ReviewProductItemLy.setImageResource(R.drawable.full_star)
                holder.binding.star5ReviewProductItemLy.setImageResource(R.drawable.full_star)
            }
        }
    }
}