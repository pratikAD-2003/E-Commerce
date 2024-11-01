package com.pycreation.e_commerce.common.search.filter

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.pycreation.e_commerce.R
import com.pycreation.e_commerce.common.search.ProductList
import com.pycreation.e_commerce.common.search.SearchFrag
import com.pycreation.e_commerce.common.search.SearchProList
import com.pycreation.e_commerce.consumer.dashboard.ConsumerDashboard
import com.pycreation.e_commerce.consumer.dashboard.tabs.Home
import com.pycreation.e_commerce.databinding.FragmentFilterPageBinding
import java.text.NumberFormat
import java.util.Currency
import kotlin.math.max

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class FilterPage : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentFilterPageBinding

    private val brand: String = "null"
    private var minPrice: Int = -1
    private var maxPrice: Int = -1
    private var minRating: Int = -1
    private val maxRating: Int = -1
    private var productCategory: String = "null"
    private var subCategory: String = "null"    // Sort by ratings descending
    private var keyword: String = "null"
    private lateinit var type: String

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
        binding = FragmentFilterPageBinding.inflate(inflater, container, false)

        type = arguments?.getString("from")!!
        if (arguments?.getString("keyword") != null) {
            keyword = arguments?.getString("keyword")!!
        }
        if (arguments?.getString("sub_Category") != null) {
            subCategory = arguments?.getString("sub_Category")!!
        }

        binding.backFromFilterPage.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        setupPriceSlider()
        setupFilter()
        setCategorySetup()

        binding.applyFilterBtnFilterPage.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("filterPage", "yes")
            bundle.putString("keyword", if (keyword!="null") keyword else "null")
            bundle.putString("sub_Category", arguments?.getString("sub_Category"))
            bundle.putString("sub_Category2", if (productCategory=="null") subCategory else null)
            bundle.putString("priceRangeMinFill", minPrice.toString())
            bundle.putString("priceRangeMaxFill", maxPrice.toString())
            bundle.putString("rating", minRating.toString())
            bundle.putString("category", productCategory)
            Log.d(
                "FILTERED_PAGE_DATA",
                "Price Min: $minPrice, Price Max: $maxPrice, Rating: $minRating, Category: $productCategory"
            )
            val productList = ProductList()
            productList.arguments = bundle
            if (type == "consumer") {
                (activity as ConsumerDashboard?)?.navigateToWithoutStackTrace(productList)
            } else {
                (activity as SearchProList?)?.navigateToWithoutStackTrace(productList)
            }

        }

        binding.priceSliderFilterPage.setLabelFormatter { value: Float ->
            val format = NumberFormat.getCurrencyInstance()
            format.maximumFractionDigits = 0
            format.currency = Currency.getInstance("INR")
            format.format(value.toDouble())
        }

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FilterPage().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    private fun buttonSelected(imageView: ImageView) {
        imageView.setImageResource(R.drawable.oval_red)
        imageView.setBackgroundDrawable(resources.getDrawable(R.drawable.black_outlined))
    }

    private fun buttonUnSelected(imageView: ImageView) {
        imageView.setImageResource(R.drawable.white_oval)
        imageView.setBackgroundDrawable(resources.getDrawable(R.drawable.grey_outlined))
    }

    private fun setupPriceSlider() {
        val values = binding.priceSliderFilterPage.values
        minPrice = values[0].toInt()
        maxPrice = values[1].toInt()
    }

    private fun setupFilter() {
        binding.oneStarRatFilterPage.setOnClickListener {
            buttonSelected(binding.oneStarImgFilterPage)
            buttonUnSelected(binding.twoStarImgFilterPage)
            buttonUnSelected(binding.threeStarImgFilterPage)
            buttonUnSelected(binding.fourStarImgFilterPage)
            buttonUnSelected(binding.fiveStarImgFilterPage)
            minRating = 1
        }

        binding.twoStarRatFilterPage.setOnClickListener {
            buttonSelected(binding.twoStarImgFilterPage)
            buttonUnSelected(binding.oneStarImgFilterPage)
            buttonUnSelected(binding.threeStarImgFilterPage)
            buttonUnSelected(binding.fourStarImgFilterPage)
            buttonUnSelected(binding.fiveStarImgFilterPage)
            minRating = 2
        }

        binding.threeStarRatFilterPage.setOnClickListener {
            buttonSelected(binding.threeStarImgFilterPage)
            buttonUnSelected(binding.twoStarImgFilterPage)
            buttonUnSelected(binding.oneStarImgFilterPage)
            buttonUnSelected(binding.fourStarImgFilterPage)
            buttonUnSelected(binding.fiveStarImgFilterPage)
            minRating = 3
        }

        binding.fourStarRatFilterPage.setOnClickListener {
            buttonSelected(binding.fourStarImgFilterPage)
            buttonUnSelected(binding.twoStarImgFilterPage)
            buttonUnSelected(binding.threeStarImgFilterPage)
            buttonUnSelected(binding.oneStarImgFilterPage)
            buttonUnSelected(binding.fiveStarImgFilterPage)
            minRating = 4
        }
        binding.fiveStarRatFilterPage.setOnClickListener {
            buttonSelected(binding.fiveStarImgFilterPage)
            buttonUnSelected(binding.twoStarImgFilterPage)
            buttonUnSelected(binding.threeStarImgFilterPage)
            buttonUnSelected(binding.fourStarImgFilterPage)
            buttonUnSelected(binding.oneStarImgFilterPage)
            minRating = 5
        }

    }

    private fun setCategorySetup() {
        binding.relevanceFilterPage.setOnClickListener {
            buttonSelected(binding.relevanceImgFilterPage)
            buttonUnSelected(binding.elecImgFilterPage)
            buttonUnSelected(binding.fashionImgFilterPage)
            buttonUnSelected(binding.homeFurImgFilterPage)
            buttonUnSelected(binding.beautyAndPerImgFiltePage)
            buttonUnSelected(binding.booksAndStaImgFilterPage)
            buttonUnSelected(binding.sportFitOutImgFilterPage)
            buttonUnSelected(binding.toysKidsImgFilterPage)
            buttonUnSelected(binding.groceImgFilterPage)
            buttonUnSelected(binding.automotiveImgFilterPage)
            buttonUnSelected(binding.healthImgFilterPage)
            buttonUnSelected(binding.gamingImgFilterPage)
            buttonUnSelected(binding.travelAndLuggImgFilterPage)
            buttonUnSelected(binding.petSupImgFilterPage)
            buttonUnSelected(binding.jewelryImgFilterPage)
            buttonUnSelected(binding.watchesAndAccImgFilterPage)
            productCategory = "Relevance"
        }

        binding.elecFilterPage.setOnClickListener {
            buttonSelected(binding.elecImgFilterPage)
            buttonUnSelected(binding.relevanceImgFilterPage)
            buttonUnSelected(binding.fashionImgFilterPage)
            buttonUnSelected(binding.homeFurImgFilterPage)
            buttonUnSelected(binding.beautyAndPerImgFiltePage)
            buttonUnSelected(binding.booksAndStaImgFilterPage)
            buttonUnSelected(binding.sportFitOutImgFilterPage)
            buttonUnSelected(binding.toysKidsImgFilterPage)
            buttonUnSelected(binding.groceImgFilterPage)
            buttonUnSelected(binding.automotiveImgFilterPage)
            buttonUnSelected(binding.healthImgFilterPage)
            buttonUnSelected(binding.gamingImgFilterPage)
            buttonUnSelected(binding.travelAndLuggImgFilterPage)
            buttonUnSelected(binding.petSupImgFilterPage)
            buttonUnSelected(binding.jewelryImgFilterPage)
            buttonUnSelected(binding.watchesAndAccImgFilterPage)
            productCategory = "Electronics"
        }

        binding.fashionFilterPage.setOnClickListener {
            buttonSelected(binding.fashionImgFilterPage)
            buttonUnSelected(binding.relevanceImgFilterPage)
            buttonUnSelected(binding.elecImgFilterPage)
            buttonUnSelected(binding.homeFurImgFilterPage)
            buttonUnSelected(binding.beautyAndPerImgFiltePage)
            buttonUnSelected(binding.booksAndStaImgFilterPage)
            buttonUnSelected(binding.sportFitOutImgFilterPage)
            buttonUnSelected(binding.toysKidsImgFilterPage)
            buttonUnSelected(binding.groceImgFilterPage)
            buttonUnSelected(binding.automotiveImgFilterPage)
            buttonUnSelected(binding.healthImgFilterPage)
            buttonUnSelected(binding.gamingImgFilterPage)
            buttonUnSelected(binding.travelAndLuggImgFilterPage)
            buttonUnSelected(binding.petSupImgFilterPage)
            buttonUnSelected(binding.jewelryImgFilterPage)
            buttonUnSelected(binding.watchesAndAccImgFilterPage)
            productCategory = "Fashion"
        }

        binding.homeAndFurFilterPage.setOnClickListener {
            buttonSelected(binding.homeFurImgFilterPage)
            buttonUnSelected(binding.fashionImgFilterPage)
            buttonUnSelected(binding.relevanceImgFilterPage)
            buttonUnSelected(binding.elecImgFilterPage)
            buttonUnSelected(binding.beautyAndPerImgFiltePage)
            buttonUnSelected(binding.booksAndStaImgFilterPage)
            buttonUnSelected(binding.sportFitOutImgFilterPage)
            buttonUnSelected(binding.toysKidsImgFilterPage)
            buttonUnSelected(binding.groceImgFilterPage)
            buttonUnSelected(binding.automotiveImgFilterPage)
            buttonUnSelected(binding.healthImgFilterPage)
            buttonUnSelected(binding.gamingImgFilterPage)
            buttonUnSelected(binding.travelAndLuggImgFilterPage)
            buttonUnSelected(binding.petSupImgFilterPage)
            buttonUnSelected(binding.jewelryImgFilterPage)
            buttonUnSelected(binding.watchesAndAccImgFilterPage)
            productCategory = "Home & Furniture"
        }

        binding.beautyAndPersonalFilterPage.setOnClickListener {
            buttonSelected(binding.beautyAndPerImgFiltePage)
            buttonUnSelected(binding.fashionImgFilterPage)
            buttonUnSelected(binding.relevanceImgFilterPage)
            buttonUnSelected(binding.elecImgFilterPage)
            buttonUnSelected(binding.homeFurImgFilterPage)
            buttonUnSelected(binding.booksAndStaImgFilterPage)
            buttonUnSelected(binding.sportFitOutImgFilterPage)
            buttonUnSelected(binding.toysKidsImgFilterPage)
            buttonUnSelected(binding.groceImgFilterPage)
            buttonUnSelected(binding.automotiveImgFilterPage)
            buttonUnSelected(binding.healthImgFilterPage)
            buttonUnSelected(binding.gamingImgFilterPage)
            buttonUnSelected(binding.travelAndLuggImgFilterPage)
            buttonUnSelected(binding.petSupImgFilterPage)
            buttonUnSelected(binding.jewelryImgFilterPage)
            buttonUnSelected(binding.watchesAndAccImgFilterPage)
            productCategory = "Beauty & Personal Care"
        }

        binding.booksAndStaFilterPage.setOnClickListener {
            buttonSelected(binding.booksAndStaImgFilterPage)
            buttonUnSelected(binding.fashionImgFilterPage)
            buttonUnSelected(binding.relevanceImgFilterPage)
            buttonUnSelected(binding.elecImgFilterPage)
            buttonUnSelected(binding.homeFurImgFilterPage)
            buttonUnSelected(binding.beautyAndPerImgFiltePage)
            buttonUnSelected(binding.sportFitOutImgFilterPage)
            buttonUnSelected(binding.toysKidsImgFilterPage)
            buttonUnSelected(binding.groceImgFilterPage)
            buttonUnSelected(binding.automotiveImgFilterPage)
            buttonUnSelected(binding.healthImgFilterPage)
            buttonUnSelected(binding.gamingImgFilterPage)
            buttonUnSelected(binding.travelAndLuggImgFilterPage)
            buttonUnSelected(binding.petSupImgFilterPage)
            buttonUnSelected(binding.jewelryImgFilterPage)
            buttonUnSelected(binding.watchesAndAccImgFilterPage)
            productCategory = "Books & Stationery"
        }

        binding.sportsFitOutFilterPage.setOnClickListener {
            buttonSelected(binding.sportFitOutImgFilterPage)
            buttonUnSelected(binding.fashionImgFilterPage)
            buttonUnSelected(binding.relevanceImgFilterPage)
            buttonUnSelected(binding.elecImgFilterPage)
            buttonUnSelected(binding.homeFurImgFilterPage)
            buttonUnSelected(binding.beautyAndPerImgFiltePage)
            buttonUnSelected(binding.booksAndStaImgFilterPage)
            buttonUnSelected(binding.toysKidsImgFilterPage)
            buttonUnSelected(binding.groceImgFilterPage)
            buttonUnSelected(binding.automotiveImgFilterPage)
            buttonUnSelected(binding.healthImgFilterPage)
            buttonUnSelected(binding.gamingImgFilterPage)
            buttonUnSelected(binding.travelAndLuggImgFilterPage)
            buttonUnSelected(binding.petSupImgFilterPage)
            buttonUnSelected(binding.jewelryImgFilterPage)
            buttonUnSelected(binding.watchesAndAccImgFilterPage)
            productCategory = "Sports,Fitness & Outdoors"
        }

        binding.toysKidsFilterPage.setOnClickListener {
            buttonSelected(binding.toysKidsImgFilterPage)
            buttonUnSelected(binding.fashionImgFilterPage)
            buttonUnSelected(binding.relevanceImgFilterPage)
            buttonUnSelected(binding.elecImgFilterPage)
            buttonUnSelected(binding.homeFurImgFilterPage)
            buttonUnSelected(binding.beautyAndPerImgFiltePage)
            buttonUnSelected(binding.booksAndStaImgFilterPage)
            buttonUnSelected(binding.sportFitOutImgFilterPage)
            buttonUnSelected(binding.groceImgFilterPage)
            buttonUnSelected(binding.automotiveImgFilterPage)
            buttonUnSelected(binding.healthImgFilterPage)
            buttonUnSelected(binding.gamingImgFilterPage)
            buttonUnSelected(binding.travelAndLuggImgFilterPage)
            buttonUnSelected(binding.petSupImgFilterPage)
            buttonUnSelected(binding.jewelryImgFilterPage)
            buttonUnSelected(binding.watchesAndAccImgFilterPage)
            productCategory = "Toys,Kids & Baby Products"
        }

        binding.groceFilterPage.setOnClickListener {
            buttonUnSelected(binding.fashionImgFilterPage)
            buttonUnSelected(binding.relevanceImgFilterPage)
            buttonUnSelected(binding.elecImgFilterPage)
            buttonUnSelected(binding.homeFurImgFilterPage)
            buttonUnSelected(binding.beautyAndPerImgFiltePage)
            buttonUnSelected(binding.booksAndStaImgFilterPage)
            buttonUnSelected(binding.sportFitOutImgFilterPage)
            buttonUnSelected(binding.toysKidsImgFilterPage)
            buttonSelected(binding.groceImgFilterPage)
            buttonUnSelected(binding.automotiveImgFilterPage)
            buttonUnSelected(binding.healthImgFilterPage)
            buttonUnSelected(binding.gamingImgFilterPage)
            buttonUnSelected(binding.travelAndLuggImgFilterPage)
            buttonUnSelected(binding.petSupImgFilterPage)
            buttonUnSelected(binding.jewelryImgFilterPage)
            buttonUnSelected(binding.watchesAndAccImgFilterPage)
            productCategory = "Groceries & Essentials"
        }

        binding.automotiveFilterPage.setOnClickListener {
            buttonUnSelected(binding.fashionImgFilterPage)
            buttonUnSelected(binding.relevanceImgFilterPage)
            buttonUnSelected(binding.elecImgFilterPage)
            buttonUnSelected(binding.homeFurImgFilterPage)
            buttonUnSelected(binding.beautyAndPerImgFiltePage)
            buttonUnSelected(binding.booksAndStaImgFilterPage)
            buttonUnSelected(binding.sportFitOutImgFilterPage)
            buttonUnSelected(binding.toysKidsImgFilterPage)
            buttonUnSelected(binding.groceImgFilterPage)
            buttonSelected(binding.automotiveImgFilterPage)
            buttonUnSelected(binding.healthImgFilterPage)
            buttonUnSelected(binding.gamingImgFilterPage)
            buttonUnSelected(binding.travelAndLuggImgFilterPage)
            buttonUnSelected(binding.petSupImgFilterPage)
            buttonUnSelected(binding.jewelryImgFilterPage)
            buttonUnSelected(binding.watchesAndAccImgFilterPage)
            productCategory = "Automotive"
        }

        binding.healthFilterPage.setOnClickListener {
            buttonUnSelected(binding.fashionImgFilterPage)
            buttonUnSelected(binding.relevanceImgFilterPage)
            buttonUnSelected(binding.elecImgFilterPage)
            buttonUnSelected(binding.homeFurImgFilterPage)
            buttonUnSelected(binding.beautyAndPerImgFiltePage)
            buttonUnSelected(binding.booksAndStaImgFilterPage)
            buttonUnSelected(binding.sportFitOutImgFilterPage)
            buttonUnSelected(binding.toysKidsImgFilterPage)
            buttonUnSelected(binding.groceImgFilterPage)
            buttonUnSelected(binding.automotiveImgFilterPage)
            buttonSelected(binding.healthImgFilterPage)
            buttonUnSelected(binding.gamingImgFilterPage)
            buttonUnSelected(binding.travelAndLuggImgFilterPage)
            buttonUnSelected(binding.petSupImgFilterPage)
            buttonUnSelected(binding.jewelryImgFilterPage)
            buttonUnSelected(binding.watchesAndAccImgFilterPage)
            productCategory = "Health & Nutrition"
        }

        binding.gamingFilterPage.setOnClickListener {
            buttonUnSelected(binding.fashionImgFilterPage)
            buttonUnSelected(binding.relevanceImgFilterPage)
            buttonUnSelected(binding.elecImgFilterPage)
            buttonUnSelected(binding.homeFurImgFilterPage)
            buttonUnSelected(binding.beautyAndPerImgFiltePage)
            buttonUnSelected(binding.booksAndStaImgFilterPage)
            buttonUnSelected(binding.sportFitOutImgFilterPage)
            buttonUnSelected(binding.toysKidsImgFilterPage)
            buttonUnSelected(binding.groceImgFilterPage)
            buttonUnSelected(binding.automotiveImgFilterPage)
            buttonUnSelected(binding.healthImgFilterPage)
            buttonSelected(binding.gamingImgFilterPage)
            buttonUnSelected(binding.travelAndLuggImgFilterPage)
            buttonUnSelected(binding.petSupImgFilterPage)
            buttonUnSelected(binding.jewelryImgFilterPage)
            buttonUnSelected(binding.watchesAndAccImgFilterPage)
            productCategory = "Gaming"
        }

        binding.travelAndLuggFilterPage.setOnClickListener {
            buttonUnSelected(binding.fashionImgFilterPage)
            buttonUnSelected(binding.relevanceImgFilterPage)
            buttonUnSelected(binding.elecImgFilterPage)
            buttonUnSelected(binding.homeFurImgFilterPage)
            buttonUnSelected(binding.beautyAndPerImgFiltePage)
            buttonUnSelected(binding.booksAndStaImgFilterPage)
            buttonUnSelected(binding.sportFitOutImgFilterPage)
            buttonUnSelected(binding.toysKidsImgFilterPage)
            buttonUnSelected(binding.groceImgFilterPage)
            buttonUnSelected(binding.automotiveImgFilterPage)
            buttonUnSelected(binding.healthImgFilterPage)
            buttonUnSelected(binding.gamingImgFilterPage)
            buttonSelected(binding.travelAndLuggImgFilterPage)
            buttonUnSelected(binding.petSupImgFilterPage)
            buttonUnSelected(binding.jewelryImgFilterPage)
            buttonUnSelected(binding.watchesAndAccImgFilterPage)
            productCategory = "Travel & Luggage"
        }

        binding.petSupFilterPage.setOnClickListener {
            buttonUnSelected(binding.fashionImgFilterPage)
            buttonUnSelected(binding.relevanceImgFilterPage)
            buttonUnSelected(binding.elecImgFilterPage)
            buttonUnSelected(binding.homeFurImgFilterPage)
            buttonUnSelected(binding.beautyAndPerImgFiltePage)
            buttonUnSelected(binding.booksAndStaImgFilterPage)
            buttonUnSelected(binding.sportFitOutImgFilterPage)
            buttonUnSelected(binding.toysKidsImgFilterPage)
            buttonUnSelected(binding.groceImgFilterPage)
            buttonUnSelected(binding.automotiveImgFilterPage)
            buttonUnSelected(binding.healthImgFilterPage)
            buttonUnSelected(binding.gamingImgFilterPage)
            buttonUnSelected(binding.travelAndLuggImgFilterPage)
            buttonSelected(binding.petSupImgFilterPage)
            buttonUnSelected(binding.jewelryImgFilterPage)
            buttonUnSelected(binding.watchesAndAccImgFilterPage)
            productCategory = "Pet Supplies"
        }

        binding.jewelryFilterPage.setOnClickListener {
            buttonUnSelected(binding.fashionImgFilterPage)
            buttonUnSelected(binding.relevanceImgFilterPage)
            buttonUnSelected(binding.elecImgFilterPage)
            buttonUnSelected(binding.homeFurImgFilterPage)
            buttonUnSelected(binding.beautyAndPerImgFiltePage)
            buttonUnSelected(binding.booksAndStaImgFilterPage)
            buttonUnSelected(binding.sportFitOutImgFilterPage)
            buttonUnSelected(binding.toysKidsImgFilterPage)
            buttonUnSelected(binding.groceImgFilterPage)
            buttonUnSelected(binding.automotiveImgFilterPage)
            buttonUnSelected(binding.healthImgFilterPage)
            buttonUnSelected(binding.gamingImgFilterPage)
            buttonUnSelected(binding.travelAndLuggImgFilterPage)
            buttonUnSelected(binding.petSupImgFilterPage)
            buttonSelected(binding.jewelryImgFilterPage)
            buttonUnSelected(binding.watchesAndAccImgFilterPage)
            productCategory = "Jewelry"
        }

        binding.watchesAndAccFilterPage.setOnClickListener {
            buttonUnSelected(binding.fashionImgFilterPage)
            buttonUnSelected(binding.relevanceImgFilterPage)
            buttonUnSelected(binding.elecImgFilterPage)
            buttonUnSelected(binding.homeFurImgFilterPage)
            buttonUnSelected(binding.beautyAndPerImgFiltePage)
            buttonUnSelected(binding.booksAndStaImgFilterPage)
            buttonUnSelected(binding.sportFitOutImgFilterPage)
            buttonUnSelected(binding.toysKidsImgFilterPage)
            buttonUnSelected(binding.groceImgFilterPage)
            buttonUnSelected(binding.automotiveImgFilterPage)
            buttonUnSelected(binding.healthImgFilterPage)
            buttonUnSelected(binding.gamingImgFilterPage)
            buttonUnSelected(binding.travelAndLuggImgFilterPage)
            buttonUnSelected(binding.petSupImgFilterPage)
            buttonUnSelected(binding.jewelryImgFilterPage)
            buttonSelected(binding.watchesAndAccImgFilterPage)
            productCategory = "Watches & Accessories"
        }
    }
}