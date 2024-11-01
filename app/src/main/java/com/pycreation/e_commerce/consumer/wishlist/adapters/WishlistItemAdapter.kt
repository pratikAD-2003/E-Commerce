package com.pycreation.e_commerce.consumer.wishlist.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.pycreation.e_commerce.R
import com.pycreation.e_commerce.common.productdetails.ProductLifeCycle
import com.pycreation.e_commerce.consumer.wishlist.models.response.Wishlist
import com.pycreation.e_commerce.databinding.WishlistItemLyBinding
import java.text.NumberFormat
import java.util.Locale

class WishlistItemAdapter(
    private val context: Context,
    private val list: List<Wishlist>,
    private val onAddToCart: (String) -> Unit,
    private val onRemoveItem: (String) -> Unit
) :
    Adapter<WishlistItemAdapter.WishlistItemHolder>() {
    inner class WishlistItemHolder(val binding: WishlistItemLyBinding) :
        ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WishlistItemHolder {
        val binding =
            WishlistItemLyBinding.inflate(LayoutInflater.from(context), parent, false)
        return WishlistItemHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: WishlistItemHolder, position: Int) {
        val currentProduct = list[position].productDetails
        Glide.with(context).load(currentProduct.productImages[0])
            .into(holder.binding.productPicWishlistItem)
        setRatings(reviewAverage = currentProduct.ratings, holder)
        holder.binding.titleWishlistItem.text = currentProduct.productName
        holder.binding.orgPriceWishlistItem.text =
            "₹" + formatNumberWithCommas(currentProduct.productPrice.toInt())
        holder.binding.sellingPriceWishlistItem.text =
            "₹" + formatNumberWithCommas(currentProduct.productSellingPrice.toInt())
        holder.binding.ratingCountWishlistItem.text = "${currentProduct.ratings}"
        holder.binding.reviewCountWishlistItem.text =
            "(${formatNumberWithCommas(currentProduct.reviewsCount)} reviews)"
        holder.binding.dropDownPercentageWishlistItem.text = calculatePriceDropPercentage(
            currentProduct.productPrice.toInt(),
            currentProduct.productSellingPrice.toInt()
        ).toString() + "% Price drop"
        holder.binding.returnPolicyWishlistItem.text =
            "Return Policy : " + currentProduct.returnPolicy

        holder.itemView.setOnClickListener {
            val gson = Gson()
            val data = gson.toJson(currentProduct)
            val intent = Intent(context, ProductLifeCycle::class.java)
            intent.putExtra("data", data)
            context.startActivity(intent)
        }

        holder.binding.addToCartWishlistItem.setOnClickListener {
            onAddToCart(currentProduct.productUid)
        }

        holder.binding.removeFromWishItem.setOnClickListener {
            onRemoveItem(currentProduct.productUid)
        }
    }

    private fun calculatePriceDropPercentage(originalPrice: Int, sellingPrice: Int): Int {
        return (((originalPrice - sellingPrice).toDouble() / originalPrice) * 100).toInt()
    }

    private fun formatNumberWithCommas(number: Int): String {
        val formatter = NumberFormat.getInstance(Locale("en", "IN"))
        return formatter.format(number)
    }

    private fun setRatings(reviewAverage: Double, holder: WishlistItemHolder) {

        if (reviewAverage > 0 && reviewAverage < 1) {
            holder.binding.star1Wishlist.setImageResource(R.drawable.half_rating)
            holder.binding.star2Wishlist.setImageResource(R.drawable.blank_star)
            holder.binding.star3Wishlist.setImageResource(R.drawable.blank_star)
            holder.binding.star4Wishlist.setImageResource(R.drawable.blank_star)
            holder.binding.star5Wishlist.setImageResource(R.drawable.blank_star)
        } else if (reviewAverage >= 1 && reviewAverage < 1.5) {
            holder.binding.star1Wishlist.setImageResource(R.drawable.full_star)
            holder.binding.star2Wishlist.setImageResource(R.drawable.blank_star)
            holder.binding.star3Wishlist.setImageResource(R.drawable.blank_star)
            holder.binding.star4Wishlist.setImageResource(R.drawable.blank_star)
            holder.binding.star5Wishlist.setImageResource(R.drawable.blank_star)
        } else if (reviewAverage >= 1.5 && reviewAverage < 2) {
            holder.binding.star1Wishlist.setImageResource(R.drawable.full_star)
            holder.binding.star2Wishlist.setImageResource(R.drawable.half_rating)
            holder.binding.star3Wishlist.setImageResource(R.drawable.blank_star)
            holder.binding.star4Wishlist.setImageResource(R.drawable.blank_star)
            holder.binding.star5Wishlist.setImageResource(R.drawable.blank_star)
        } else if (reviewAverage >= 2 && reviewAverage < 2.5) {
            holder.binding.star1Wishlist.setImageResource(R.drawable.full_star)
            holder.binding.star2Wishlist.setImageResource(R.drawable.full_star)
            holder.binding.star3Wishlist.setImageResource(R.drawable.blank_star)
            holder.binding.star4Wishlist.setImageResource(R.drawable.blank_star)
            holder.binding.star5Wishlist.setImageResource(R.drawable.blank_star)
        } else if (reviewAverage >= 2.5 && reviewAverage < 3) {
            holder.binding.star1Wishlist.setImageResource(R.drawable.full_star)
            holder.binding.star2Wishlist.setImageResource(R.drawable.full_star)
            holder.binding.star3Wishlist.setImageResource(R.drawable.half_rating)
            holder.binding.star4Wishlist.setImageResource(R.drawable.blank_star)
            holder.binding.star5Wishlist.setImageResource(R.drawable.blank_star)
        } else if (reviewAverage >= 3 && reviewAverage < 3.5) {
            holder.binding.star1Wishlist.setImageResource(R.drawable.full_star)
            holder.binding.star2Wishlist.setImageResource(R.drawable.full_star)
            holder.binding.star3Wishlist.setImageResource(R.drawable.full_star)
            holder.binding.star4Wishlist.setImageResource(R.drawable.blank_star)
            holder.binding.star5Wishlist.setImageResource(R.drawable.blank_star)
        } else if (reviewAverage >= 3.5 && reviewAverage < 4) {
            holder.binding.star1Wishlist.setImageResource(R.drawable.full_star)
            holder.binding.star2Wishlist.setImageResource(R.drawable.full_star)
            holder.binding.star3Wishlist.setImageResource(R.drawable.full_star)
            holder.binding.star4Wishlist.setImageResource(R.drawable.half_rating)
            holder.binding.star5Wishlist.setImageResource(R.drawable.blank_star)
        } else if (reviewAverage >= 4 && reviewAverage < 4.5) {
            holder.binding.star1Wishlist.setImageResource(R.drawable.full_star)
            holder.binding.star2Wishlist.setImageResource(R.drawable.full_star)
            holder.binding.star3Wishlist.setImageResource(R.drawable.full_star)
            holder.binding.star4Wishlist.setImageResource(R.drawable.full_star)
            holder.binding.star5Wishlist.setImageResource(R.drawable.blank_star)
        } else if (reviewAverage >= 4.5 && reviewAverage < 5) {
            holder.binding.star1Wishlist.setImageResource(R.drawable.full_star)
            holder.binding.star2Wishlist.setImageResource(R.drawable.full_star)
            holder.binding.star3Wishlist.setImageResource(R.drawable.full_star)
            holder.binding.star4Wishlist.setImageResource(R.drawable.full_star)
            holder.binding.star5Wishlist.setImageResource(R.drawable.half_rating)
        } else if (reviewAverage >= 5) {
            holder.binding.star1Wishlist.setImageResource(R.drawable.full_star)
            holder.binding.star2Wishlist.setImageResource(R.drawable.full_star)
            holder.binding.star3Wishlist.setImageResource(R.drawable.full_star)
            holder.binding.star4Wishlist.setImageResource(R.drawable.full_star)
            holder.binding.star5Wishlist.setImageResource(R.drawable.full_star)
        }
    }
}