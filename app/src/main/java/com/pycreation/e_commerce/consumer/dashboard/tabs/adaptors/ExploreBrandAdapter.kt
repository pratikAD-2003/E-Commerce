package com.pycreation.e_commerce.consumer.dashboard.tabs.adaptors

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.pycreation.e_commerce.Constants
import com.pycreation.e_commerce.common.search.ProductList
import com.pycreation.e_commerce.consumer.dashboard.ConsumerDashboard
import com.pycreation.e_commerce.consumer.dashboard.sub_category.res.SubCategoryResModel
import com.pycreation.e_commerce.databinding.ExplorePageBrandItemBinding
import com.pycreation.e_commerce.databinding.HomePageSubCatItemsLyBinding

class ExploreBrandAdapter(
    private val context: Context,
    private val subCategoryResModel: SubCategoryResModel
) : Adapter<ExploreBrandAdapter.SubCatHomeHolder>() {
    inner class SubCatHomeHolder(val binding: ExplorePageBrandItemBinding) :
        ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubCatHomeHolder {
        val binding =
            ExplorePageBrandItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return SubCatHomeHolder(binding)
    }

    override fun getItemCount(): Int {
        return subCategoryResModel.size
    }

    override fun onBindViewHolder(holder: SubCatHomeHolder, position: Int) {
        val current = subCategoryResModel[position]
        Glide.with(context).load(current.subCategoryImage).into(holder.binding.subItemImg)
        holder.binding.subItemSub.text = current.subCategory

        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("by_brand", current.subCategory)
            bundle.putString("by_brand_cat",if (current.category == Constants.PHONES_CATEGORY) "Mobiles & Accessories" else "Laptops & Computers")
            val productListFrag = ProductList()
            productListFrag.arguments = bundle
            (context as ConsumerDashboard?)?.navigateTo(productListFrag)
        }
    }
}