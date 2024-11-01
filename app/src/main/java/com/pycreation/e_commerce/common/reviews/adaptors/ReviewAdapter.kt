package com.pycreation.e_commerce.common.reviews.adaptors

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.pycreation.e_commerce.common.reviews.models.ReviewModelItem
import com.pycreation.e_commerce.databinding.ProductReviewItemLyBinding
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class ReviewAdapter(private val context: Context, private val list: List<ReviewModelItem>) :
    Adapter<ReviewAdapter.ReviewHolder>() {
    inner class ReviewHolder(val binding: ProductReviewItemLyBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewHolder {
        val binding =
            ProductReviewItemLyBinding.inflate(LayoutInflater.from(context), parent, false)
        return ReviewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ReviewHolder, position: Int) {
        val current = list[position]
        holder.binding.reviewerName.text = current.reviewerName
        holder.binding.reviewerText.text = current.reviewText
        holder.binding.reviewerDate.text = convertToDateMonthYear(current.timestamp)
        holder.binding.reviewerRating.text = "Rated - " + current.rating
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertToDateMonthYear(input: String): String {
        val zonedDateTime = ZonedDateTime.parse(input)

        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")

        return zonedDateTime.format(formatter)
    }
}