package com.pycreation.e_commerce.consumer.orders.adaptors

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.pycreation.e_commerce.R
import com.pycreation.e_commerce.consumer.dashboard.ConsumerDashboard
import com.pycreation.e_commerce.consumer.orders.activities.OrderItemDetails
import com.pycreation.e_commerce.consumer.orders.res.Order
import com.pycreation.e_commerce.databinding.MyOrdersLyItemBinding
import kotlin.math.acos

class MyOrdersAdapter(val context: Context, val list: List<Order>) :
    Adapter<MyOrdersAdapter.MyOrderHolder>() {
    public inner class MyOrderHolder(val binding: MyOrdersLyItemBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyOrderHolder {
        val binding = MyOrdersLyItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyOrderHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyOrderHolder, position: Int) {
        val currentOrder = list[position]
        when (currentOrder.orderStatus) {
            "Placed" -> {

            }

            "Shipped" -> {
                holder.binding.shippedOrderLineItem.setBackgroundColor(context.resources.getColor(R.color.orange))
                holder.binding.shippedCircleItem.setImageResource(R.drawable.oval_red)
                holder.binding.shippedTextItem.setTextColor(context.resources.getColor(R.color.orange))
                holder.binding.shippedIconItem.imageTintList =
                    ColorStateList.valueOf(context.resources.getColor(R.color.orange))
            }

            "Out of Delivery" -> {
                holder.binding.shippedOrderLineItem.setBackgroundColor(context.resources.getColor(R.color.orange))
                holder.binding.shippedCircleItem.setImageResource(R.drawable.oval_red)
                holder.binding.shippedTextItem.setTextColor(context.resources.getColor(R.color.orange))
                holder.binding.shippedIconItem.imageTintList =
                    ColorStateList.valueOf(context.resources.getColor(R.color.orange))

                holder.binding.outOfOrderLineItem.setBackgroundColor(context.resources.getColor(R.color.orange))
                holder.binding.outOfDeliveryCircleItem.setImageResource(R.drawable.oval_red)
                holder.binding.outOfDeliveryTextItem.setTextColor(context.resources.getColor(R.color.orange))
                holder.binding.outOfDeliveryIconItem.imageTintList =
                    ColorStateList.valueOf(context.resources.getColor(R.color.orange))
            }

            "Delivered" -> {
                holder.binding.shippedOrderLineItem.setBackgroundColor(context.resources.getColor(R.color.orange))
                holder.binding.shippedCircleItem.setImageResource(R.drawable.oval_red)
                holder.binding.shippedTextItem.setTextColor(context.resources.getColor(R.color.orange))
                holder.binding.shippedIconItem.imageTintList =
                    ColorStateList.valueOf(context.resources.getColor(R.color.orange))

                holder.binding.outOfOrderLineItem.setBackgroundColor(context.resources.getColor(R.color.orange))
                holder.binding.outOfDeliveryCircleItem.setImageResource(R.drawable.oval_red)
                holder.binding.outOfDeliveryTextItem.setTextColor(context.resources.getColor(R.color.orange))
                holder.binding.outOfDeliveryIconItem.imageTintList =
                    ColorStateList.valueOf(context.resources.getColor(R.color.orange))

                holder.binding.deliveredLineItem.setBackgroundColor(context.resources.getColor(R.color.orange))
                holder.binding.deliveredCircleItem.setImageResource(R.drawable.oval_red)
                holder.binding.deliveryTextItem.setTextColor(context.resources.getColor(R.color.orange))
                holder.binding.deliveredIconItem.imageTintList =
                    ColorStateList.valueOf(context.resources.getColor(R.color.orange))
            }

            "Canceled" -> {

            }
        }
        Glide.with(context).load(currentOrder.items[0].productDetails.productImages[0])
            .into(holder.binding.ordersImgItem)
        if (currentOrder.items.size > 1) {
            holder.binding.ordersTitleItem.text = "More than 1 Items Ordered..."
            holder.binding.imageCountLyOrdersItem.visibility = View.VISIBLE
            holder.binding.imageCountItemOrders.text =
                "+" + (currentOrder.items.size - 1).toString()
        } else {
            holder.binding.imageCountLyOrdersItem.visibility = View.GONE
            holder.binding.ordersTitleItem.text =
                currentOrder.items[0].productDetails.productName
        }
        holder.binding.ordersStatusItem.text = currentOrder.orderStatus

        holder.itemView.setOnClickListener {
            val gson = Gson()
            val data = gson.toJson(currentOrder)
            val bundle = Bundle()
            val orderItemDetailsFrag = OrderItemDetails()
            bundle.putString("data", data)
            orderItemDetailsFrag.arguments = bundle
            (context as ConsumerDashboard?)?.navigateTo(orderItemDetailsFrag)
        }
    }
}