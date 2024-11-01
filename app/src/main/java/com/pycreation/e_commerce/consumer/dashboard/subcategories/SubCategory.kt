package com.pycreation.e_commerce.consumer.dashboard.subcategories

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.pycreation.e_commerce.Constants
import com.pycreation.e_commerce.R
import com.pycreation.e_commerce.common.search.ProductList
import com.pycreation.e_commerce.common.search.SearchProList
import com.pycreation.e_commerce.consumer.dashboard.ConsumerDashboard
import com.pycreation.e_commerce.databinding.FragmentSubCategoryBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SubCategory : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentSubCategoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSubCategoryBinding.inflate(inflater, container, false)
        val category = arguments?.getString("subCategory")
        binding.selectedSubCatSC.text = Constants.productCategories[category?.toInt()?.minus(1)!!]
        val adapter = ArrayAdapter<String>(
            requireContext(),
            R.layout.sub_cat_listview_item_ly,
            R.id.subCategoryName,
            getSubList(category)
        )
        binding.subCategoryListSubCat.adapter = adapter

        binding.subCategoryListSubCat.setOnItemClickListener { parent, view, position, id ->
            val clickedItem = getSubList(category)[position]
            val bundle = Bundle()
            bundle.putString("sub_Category", clickedItem)
            val productListFrag = ProductList()
            productListFrag.arguments = bundle
            (activity as ConsumerDashboard?)?.navigateTo(productListFrag)
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SubCategory().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun getSubList(category: String): List<String> {
        when (category.toInt()) {
            1 -> {
                return Constants.electronicsCat
            }

            2 -> {
                return Constants.fashionCat
            }

            3 -> {
                return Constants.homeAndFurnitureCat
            }

            4 -> {
                return Constants.beautyAndPersonalCareCat
            }

            5 -> {
                return Constants.booksAndStationeryCat
            }

            6 -> {
                return Constants.sportFitnessOutdoorsCat
            }

            7 -> {
                return Constants.toysKidsBabyProCat
            }

            8 -> {
                return Constants.groceriesAndEssentialsCat
            }

            9 -> {
                return Constants.automotiveCat
            }

            10 -> {
                return Constants.healthAndNutritionCat
            }

            11 -> {
                return Constants.gamingCat
            }

            12 -> {
                return Constants.travelAndLuggageCat
            }

            13 -> {
                return Constants.petSuppliesCat
            }

            14 -> {
                return Constants.jewelryCat
            }

            15 -> {
                return Constants.watchesAndAccessories
            }

            else -> return listOf()
        }
    }
}