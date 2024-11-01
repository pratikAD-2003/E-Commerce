package com.pycreation.e_commerce.consumer.addToCart.adaptors

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
import com.pycreation.e_commerce.databinding.CartProductItemBinding
import com.pycreation.e_commerce.databinding.PlacedProductsItemsBinding
import com.pycreation.e_commerce.databinding.SellerProductItemLyBinding
import java.text.NumberFormat
import java.util.Locale

class PlacedProductAdapter(
    private val context: Context,
    private val list: List<Product>,
    private val quantityList: List<String>,
    private val onIncrease: (String) -> Unit,
    private val onDecrease: (String) -> Unit
) :
    Adapter<PlacedProductAdapter.CartProductHolder>() {
    inner class CartProductHolder(val binding: PlacedProductsItemsBinding) :
        ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartProductHolder {
        val binding =
            PlacedProductsItemsBinding.inflate(LayoutInflater.from(context), parent, false)
        return CartProductHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: CartProductHolder, position: Int) {
        val currentProduct = list[position]
        Glide.with(context).load(currentProduct.productImages[0])
            .into(holder.binding.productPicCartItem)
        setRatings(reviewAverage = currentProduct.ratings, holder)
        holder.binding.titleCartItem.text = currentProduct.productName
        holder.binding.orgPriceCartItem.text =
            "₹" + formatNumberWithCommas(currentProduct.productPrice.toInt())
        holder.binding.sellingPriceCartItem.text =
            "₹" + formatNumberWithCommas(currentProduct.productSellingPrice.toInt())
        holder.binding.ratingCountCartItem.text = "${currentProduct.ratings}"
        holder.binding.returnPolicyCartItem.text = "Return Policy : " + currentProduct.returnPolicy
        holder.binding.quantityItemText.text = quantityList[position]

        if (currentProduct.cashOnDelivery) {
            if (currentProduct.freeDelivery) {
                holder.binding.cashOnDeliveryCartItem.text = "Free Delivery"
            } else {
                holder.binding.cashOnDeliveryCartItem.text = "COD Available"
            }
        } else {
            holder.binding.cashOnDeliveryCartItem.text = "COD - Not Available"
        }

        holder.itemView.setOnClickListener {
            val gson = Gson()
            val data = gson.toJson(currentProduct)
            val intent = Intent(context, ProductLifeCycle::class.java)
            intent.putExtra("data", data)
            context.startActivity(intent)
        }

        holder.binding.addQuantityItemBtn.setOnClickListener {
            onIncrease(currentProduct.productUid)
        }

        holder.binding.removeQuantityItemBtn.setOnClickListener {
            if (quantityList[position].toInt() != 1)
                onDecrease(currentProduct.productUid)
        }
    }

    private fun calculatePriceDropPercentage(originalPrice: Int, sellingPrice: Int): Int {
        return (((originalPrice - sellingPrice).toDouble() / originalPrice) * 100).toInt()
    }

    private fun formatNumberWithCommas(number: Int): String {
        val formatter = NumberFormat.getInstance(Locale("en", "IN"))
        return formatter.format(number)
    }

    private fun setRatings(reviewAverage: Double, holder: CartProductHolder) {

        if (reviewAverage > 0 && reviewAverage < 1) {
            holder.binding.star1Cart.setImageResource(R.drawable.half_rating)
            holder.binding.star2Cart.setImageResource(R.drawable.blank_star)
            holder.binding.star3Cart.setImageResource(R.drawable.blank_star)
            holder.binding.star4Cart.setImageResource(R.drawable.blank_star)
            holder.binding.star5Cart.setImageResource(R.drawable.blank_star)
        } else if (reviewAverage >= 1 && reviewAverage < 1.5) {
            holder.binding.star1Cart.setImageResource(R.drawable.full_star)
            holder.binding.star2Cart.setImageResource(R.drawable.blank_star)
            holder.binding.star3Cart.setImageResource(R.drawable.blank_star)
            holder.binding.star4Cart.setImageResource(R.drawable.blank_star)
            holder.binding.star5Cart.setImageResource(R.drawable.blank_star)
        } else if (reviewAverage >= 1.5 && reviewAverage < 2) {
            holder.binding.star1Cart.setImageResource(R.drawable.full_star)
            holder.binding.star2Cart.setImageResource(R.drawable.half_rating)
            holder.binding.star3Cart.setImageResource(R.drawable.blank_star)
            holder.binding.star4Cart.setImageResource(R.drawable.blank_star)
            holder.binding.star5Cart.setImageResource(R.drawable.blank_star)
        } else if (reviewAverage >= 2 && reviewAverage < 2.5) {
            holder.binding.star1Cart.setImageResource(R.drawable.full_star)
            holder.binding.star2Cart.setImageResource(R.drawable.full_star)
            holder.binding.star3Cart.setImageResource(R.drawable.blank_star)
            holder.binding.star4Cart.setImageResource(R.drawable.blank_star)
            holder.binding.star5Cart.setImageResource(R.drawable.blank_star)
        } else if (reviewAverage >= 2.5 && reviewAverage < 3) {
            holder.binding.star1Cart.setImageResource(R.drawable.full_star)
            holder.binding.star2Cart.setImageResource(R.drawable.full_star)
            holder.binding.star3Cart.setImageResource(R.drawable.half_rating)
            holder.binding.star4Cart.setImageResource(R.drawable.blank_star)
            holder.binding.star5Cart.setImageResource(R.drawable.blank_star)
        } else if (reviewAverage >= 3 && reviewAverage < 3.5) {
            holder.binding.star1Cart.setImageResource(R.drawable.full_star)
            holder.binding.star2Cart.setImageResource(R.drawable.full_star)
            holder.binding.star3Cart.setImageResource(R.drawable.full_star)
            holder.binding.star4Cart.setImageResource(R.drawable.blank_star)
            holder.binding.star5Cart.setImageResource(R.drawable.blank_star)
        } else if (reviewAverage >= 3.5 && reviewAverage < 4) {
            holder.binding.star1Cart.setImageResource(R.drawable.full_star)
            holder.binding.star2Cart.setImageResource(R.drawable.full_star)
            holder.binding.star3Cart.setImageResource(R.drawable.full_star)
            holder.binding.star4Cart.setImageResource(R.drawable.half_rating)
            holder.binding.star5Cart.setImageResource(R.drawable.blank_star)
        } else if (reviewAverage >= 4 && reviewAverage < 4.5) {
            holder.binding.star1Cart.setImageResource(R.drawable.full_star)
            holder.binding.star2Cart.setImageResource(R.drawable.full_star)
            holder.binding.star3Cart.setImageResource(R.drawable.full_star)
            holder.binding.star4Cart.setImageResource(R.drawable.full_star)
            holder.binding.star5Cart.setImageResource(R.drawable.blank_star)
        } else if (reviewAverage >= 4.5 && reviewAverage < 5) {
            holder.binding.star1Cart.setImageResource(R.drawable.full_star)
            holder.binding.star2Cart.setImageResource(R.drawable.full_star)
            holder.binding.star3Cart.setImageResource(R.drawable.full_star)
            holder.binding.star4Cart.setImageResource(R.drawable.full_star)
            holder.binding.star5Cart.setImageResource(R.drawable.half_rating)
        } else if (reviewAverage >= 5) {
            holder.binding.star1Cart.setImageResource(R.drawable.full_star)
            holder.binding.star2Cart.setImageResource(R.drawable.full_star)
            holder.binding.star3Cart.setImageResource(R.drawable.full_star)
            holder.binding.star4Cart.setImageResource(R.drawable.full_star)
            holder.binding.star5Cart.setImageResource(R.drawable.full_star)
        }
    }
}