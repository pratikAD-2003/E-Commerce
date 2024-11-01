package com.pycreation.e_commerce.admin.listening.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.pycreation.e_commerce.R
import com.pycreation.e_commerce.admin.models.productModel.Product
import com.pycreation.e_commerce.common.productdetails.ProductLifeCycle
import com.pycreation.e_commerce.databinding.SellerProductItemLyBinding
import com.pycreation.e_commerce.databinding.SmallProductItemLyBinding
import java.text.NumberFormat
import java.util.Locale

class ProductHoriAdapter(private val context: Context, private val list: List<Product>) :
    Adapter<ProductHoriAdapter.ProductHolderPAcc>() {
    inner class ProductHolderPAcc(val binding: SmallProductItemLyBinding) :
        ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductHolderPAcc {
        val binding =
            SmallProductItemLyBinding.inflate(LayoutInflater.from(context), parent, false)
        return ProductHolderPAcc(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ProductHolderPAcc, position: Int) {
        val currentProduct = list[position]
        Glide.with(context).load(currentProduct.productImages[0])
            .into(holder.binding.productPicItem)
        setRatings(reviewAverage = currentProduct.ratings, holder)
        holder.binding.titleItem.text = currentProduct.productName
        holder.binding.orgPriceItem.text =
            "₹" + formatNumberWithCommas(currentProduct.productPrice.toInt())
        holder.binding.sellingPriceItem.text =
            "₹" + formatNumberWithCommas(currentProduct.productSellingPrice.toInt())
        holder.binding.ratingCountItem.text = "${currentProduct.ratings}"
        holder.binding.reviewCountItem.text =
            "(${formatNumberWithCommas(currentProduct.reviewsCount)} reviews)"
        holder.binding.dropDownPercentageItem.text = calculatePriceDropPercentage(
            currentProduct.productPrice.toInt(),
            currentProduct.productSellingPrice.toInt()
        ).toString() + "% Price drop"

        holder.itemView.setOnClickListener {
            val gson = Gson()
            val data = gson.toJson(currentProduct)
            val intent = Intent(context, ProductLifeCycle::class.java)
            intent.putExtra("data",data)
            context.startActivity(intent)
        }
    }

    private fun calculatePriceDropPercentage(originalPrice: Int, sellingPrice: Int): Int {
        return (((originalPrice - sellingPrice).toDouble() / originalPrice) * 100).toInt()
    }

    private fun formatNumberWithCommas(number: Int): String {
        val formatter = NumberFormat.getInstance(Locale("en", "IN"))
        return formatter.format(number)
    }

    private fun setRatings(reviewAverage: Double, holder: ProductHolderPAcc) {

        if (reviewAverage > 0 && reviewAverage < 1) {
            holder.binding.star1.setImageResource(R.drawable.half_rating)
            holder.binding.star2.setImageResource(R.drawable.blank_star)
            holder.binding.star3.setImageResource(R.drawable.blank_star)
            holder.binding.star4.setImageResource(R.drawable.blank_star)
            holder.binding.star5.setImageResource(R.drawable.blank_star)
        } else if (reviewAverage >= 1 && reviewAverage < 1.5) {
            holder.binding.star1.setImageResource(R.drawable.full_star)
            holder.binding.star2.setImageResource(R.drawable.blank_star)
            holder.binding.star3.setImageResource(R.drawable.blank_star)
            holder.binding.star4.setImageResource(R.drawable.blank_star)
            holder.binding.star5.setImageResource(R.drawable.blank_star)
        } else if (reviewAverage >= 1.5 && reviewAverage < 2) {
            holder.binding.star1.setImageResource(R.drawable.full_star)
            holder.binding.star2.setImageResource(R.drawable.half_rating)
            holder.binding.star3.setImageResource(R.drawable.blank_star)
            holder.binding.star4.setImageResource(R.drawable.blank_star)
            holder.binding.star5.setImageResource(R.drawable.blank_star)
        } else if (reviewAverage >= 2 && reviewAverage < 2.5) {
            holder.binding.star1.setImageResource(R.drawable.full_star)
            holder.binding.star2.setImageResource(R.drawable.full_star)
            holder.binding.star3.setImageResource(R.drawable.blank_star)
            holder.binding.star4.setImageResource(R.drawable.blank_star)
            holder.binding.star5.setImageResource(R.drawable.blank_star)
        } else if (reviewAverage >= 2.5 && reviewAverage < 3) {
            holder.binding.star1.setImageResource(R.drawable.full_star)
            holder.binding.star2.setImageResource(R.drawable.full_star)
            holder.binding.star3.setImageResource(R.drawable.half_rating)
            holder.binding.star4.setImageResource(R.drawable.blank_star)
            holder.binding.star5.setImageResource(R.drawable.blank_star)
        } else if (reviewAverage >= 3 && reviewAverage < 3.5) {
            holder.binding.star1.setImageResource(R.drawable.full_star)
            holder.binding.star2.setImageResource(R.drawable.full_star)
            holder.binding.star3.setImageResource(R.drawable.full_star)
            holder.binding.star4.setImageResource(R.drawable.blank_star)
            holder.binding.star5.setImageResource(R.drawable.blank_star)
        } else if (reviewAverage >= 3.5 && reviewAverage < 4) {
            holder.binding.star1.setImageResource(R.drawable.full_star)
            holder.binding.star2.setImageResource(R.drawable.full_star)
            holder.binding.star3.setImageResource(R.drawable.full_star)
            holder.binding.star4.setImageResource(R.drawable.half_rating)
            holder.binding.star5.setImageResource(R.drawable.blank_star)
        } else if (reviewAverage >= 4 && reviewAverage < 4.5) {
            holder.binding.star1.setImageResource(R.drawable.full_star)
            holder.binding.star2.setImageResource(R.drawable.full_star)
            holder.binding.star3.setImageResource(R.drawable.full_star)
            holder.binding.star4.setImageResource(R.drawable.full_star)
            holder.binding.star5.setImageResource(R.drawable.blank_star)
        } else if (reviewAverage >= 4.5 && reviewAverage < 5) {
            holder.binding.star1.setImageResource(R.drawable.full_star)
            holder.binding.star2.setImageResource(R.drawable.full_star)
            holder.binding.star3.setImageResource(R.drawable.full_star)
            holder.binding.star4.setImageResource(R.drawable.full_star)
            holder.binding.star5.setImageResource(R.drawable.half_rating)
        } else if (reviewAverage >= 5) {
            holder.binding.star1.setImageResource(R.drawable.full_star)
            holder.binding.star2.setImageResource(R.drawable.full_star)
            holder.binding.star3.setImageResource(R.drawable.full_star)
            holder.binding.star4.setImageResource(R.drawable.full_star)
            holder.binding.star5.setImageResource(R.drawable.full_star)
        }
    }
}