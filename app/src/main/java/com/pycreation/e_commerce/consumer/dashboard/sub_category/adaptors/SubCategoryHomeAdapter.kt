package com.pycreation.e_commerce.consumer.dashboard.sub_category.adaptors

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.pycreation.e_commerce.common.search.ProductList
import com.pycreation.e_commerce.consumer.dashboard.ConsumerDashboard
import com.pycreation.e_commerce.consumer.dashboard.sub_category.res.SubCategoryResModel
import com.pycreation.e_commerce.databinding.HomePageSubCatItemsLyBinding

class SubCategoryHomeAdapter(
    private val context: Context,
    private val subCategoryResModel: SubCategoryResModel
) : Adapter<SubCategoryHomeAdapter.SubCatHomeHolder>() {
    inner class SubCatHomeHolder(val binding: HomePageSubCatItemsLyBinding) :
        ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubCatHomeHolder {
        val binding =
            HomePageSubCatItemsLyBinding.inflate(LayoutInflater.from(context), parent, false)
        return SubCatHomeHolder(binding)
    }

    override fun getItemCount(): Int {
        return subCategoryResModel.size
    }

    override fun onBindViewHolder(holder: SubCatHomeHolder, position: Int) {
        val current = subCategoryResModel[position]
        Glide.with(context).load(current.subCategoryImage).into(holder.binding.subItemImg)
        holder.binding.subItemSub.text = current.subCategory
        holder.binding.subItemTitle.text = current.title

        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("sub_Category", current.subCategory)
            val productListFrag = ProductList()
            productListFrag.arguments = bundle
            (context as ConsumerDashboard?)?.navigateTo(productListFrag)
        }
    }
}