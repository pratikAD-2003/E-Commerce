package com.pycreation.e_commerce.consumer.orders.adaptors

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.pycreation.e_commerce.R
import com.pycreation.e_commerce.admin.models.productModel.Product
import com.pycreation.e_commerce.common.productdetails.ProductLifeCycle
import com.pycreation.e_commerce.common.reviews.activities.ReviewScreen
import com.pycreation.e_commerce.consumer.dashboard.ConsumerDashboard
import com.pycreation.e_commerce.consumer.orders.res.Item
import com.pycreation.e_commerce.databinding.OrderedProductsItemsBinding
import com.pycreation.e_commerce.databinding.PlacedProductsItemsBinding
import java.text.NumberFormat
import java.util.Locale

class OrderedProductsAdapter(
    private val context: Context,
    private val list: List<Item>,
    private val isDelivered: String
) :
    Adapter<OrderedProductsAdapter.CartProductHolder>() {
    inner class CartProductHolder(val binding: OrderedProductsItemsBinding) :
        ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartProductHolder {
        val binding =
            OrderedProductsItemsBinding.inflate(LayoutInflater.from(context), parent, false)
        return CartProductHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: CartProductHolder, position: Int) {
        val currentProduct = list[position].productDetails
        Glide.with(context).load(currentProduct.productImages[0])
            .into(holder.binding.productPicOrderItem)
        if (isDelivered == "Delivered") {
            holder.binding.writeReviewItem.visibility = View.VISIBLE
        } else {
            holder.binding.writeReviewItem.visibility = View.GONE
        }
        holder.binding.titleOrderItem.text = currentProduct.productName
        holder.binding.orgPriceOrderItem.text =
            "₹" + formatNumberWithCommas(currentProduct.productPrice.toInt())
        holder.binding.sellingPriceOrderItem.text =
            "₹" + formatNumberWithCommas(currentProduct.productSellingPrice.toInt())
        holder.binding.orderQuantityItem.text = "Quantity : " + list[position].quantity.toString()
        holder.itemView.setOnClickListener {
            val gson = Gson()
            val data = gson.toJson(currentProduct)
            val intent = Intent(context, ProductLifeCycle::class.java)
            intent.putExtra("data", data)
            context.startActivity(intent)
        }

        holder.binding.writeReviewItem.setOnClickListener {
            val reviewScreen = ReviewScreen()
            val bundle = Bundle()
            bundle.putString("name", currentProduct.productName)
            bundle.putString("productUid", currentProduct.productUid)
            reviewScreen.arguments = bundle
            (context as ConsumerDashboard?)?.navigateTo(reviewScreen)
        }
    }

    private fun formatNumberWithCommas(number: Int): String {
        val formatter = NumberFormat.getInstance(Locale("en", "IN"))
        return formatter.format(number)
    }
}