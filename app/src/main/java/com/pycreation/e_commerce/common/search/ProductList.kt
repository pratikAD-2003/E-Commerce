package com.pycreation.e_commerce.common.search

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.pycreation.e_commerce.R
import com.pycreation.e_commerce.admin.listening.adapter.ProductAdapterPAcc
import com.pycreation.e_commerce.admin.models.productModel.Product
import com.pycreation.e_commerce.admin.models.productModel.ProductModel
import com.pycreation.e_commerce.common.productdetails.ProductLifeCycle
import com.pycreation.e_commerce.common.search.bottomsheets.BottomSheet
import com.pycreation.e_commerce.common.search.filter.FilterPage
import com.pycreation.e_commerce.consumer.dashboard.ConsumerDashboard
import com.pycreation.e_commerce.consumer.dashboard.tabs.Home
import com.pycreation.e_commerce.databinding.FragmentProductListBinding
import com.pycreation.e_commerce.retrofit.ApiClient
import com.pycreation.e_commerce.retrofit.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProductList : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentProductListBinding

    private var sortByPrice: String = "null"        // No sorting by price
    private var sortByRatings: String = "null"    // Sort by ratings descending
    private var sortByTimestamp: String = "null"    // Sort by ratings descending
    private var brand: String = "null"         // Filter by brand (case-insensitive)
    private var minPrice: Int = -1             // Minimum price
    private var maxPrice: Int = -1           // Maximum price
    private var minRating: Int = -1         // Minimum rating
    private var maxRating: Int = -1          // Maximum rating
    private var productCategory: String = "null"    // Sort by ratings descending
    private var subCategory: String = "null"    // Sort by ratings descending
    private var limit: Int = 10                   // Limit to 10 results per page
    private var page: Int = 1
    private var keyword: String = "null"

    private lateinit var adapter: ProductAdapterPAcc

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductListBinding.inflate(inflater, container, false)
        checkInternetConnection()
        val intentFilter = IntentFilter("BROADCAST_BOTTOM_SHEET")
        activity?.registerReceiver(dataReceiver, intentFilter, Context.RECEIVER_NOT_EXPORTED)

        binding.productRecyclerviewProductList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (arguments?.getString("sub_Category") != null || arguments?.getString("by_brand") != null) {
                (activity as ConsumerDashboard?)?.setHomeIcon()
                (activity as ConsumerDashboard?)?.navigateToWithoutStackTrace(Home())
            } else {
                requireActivity().finish()
            }
        }

        binding.backFromProductList.setOnClickListener {
            if (arguments?.getString("sub_Category") != null || arguments?.getString("by_brand") != null) {
                (activity as ConsumerDashboard?)?.setHomeIcon()
                (activity as ConsumerDashboard?)?.navigateToWithoutStackTrace(Home())
            } else {
                (activity as AppCompatActivity).finish()
            }
        }
        if (arguments?.getString("filterPage") != null) {
            if (arguments?.getString("keyword") != null) {
                keyword = arguments?.getString("keyword")!!
                binding.keywordProductList.setText(keyword)
            }
            if (arguments?.getString("priceRangeMinFill")!!.toInt() != -1) {
                minPrice = arguments?.getString("priceRangeMinFill")!!.toInt()
            }
            if (arguments?.getString("priceRangeMaxFill")!!.toInt() != -1) {
                maxPrice = arguments?.getString("priceRangeMaxFill")!!.toInt()
            }
            if (arguments?.getString("rating")!!.toInt() != -1) {
                minRating = arguments?.getString("rating")!!.toInt()
            }
            if (arguments?.getString("sub_Category2") != null) {
                subCategory = arguments?.getString("sub_Category2")!!
                binding.keywordProductList.setText(subCategory)
            }
            if (arguments?.getString("category") != null) {
                productCategory = arguments?.getString("category")!!
                binding.keywordProductList.setText(productCategory)
            }
            if (arguments?.getString("brand")!=null){
                brand = arguments?.getString("brand")!!
            }
            getProductFilter()
        }
        if (arguments?.getString("keyword") != null) {
            if (arguments?.getString("keyword")!!.isNotEmpty()) {
                keyword = arguments?.getString("keyword")!!
                binding.keywordProductList.setText(keyword)
                getProductFilter()
            }
        }
        if (arguments?.getString("sub_Category") != null) {
            if (arguments?.getString("sub_Category")!!.isNotEmpty()) {
                subCategory = arguments?.getString("sub_Category")!!
                binding.keywordProductList.setText(subCategory)
                getProductFilter()
            }
        }
        if (arguments?.getString("by_brand") != null) {
            if (arguments?.getString("by_brand")!!.isNotEmpty()) {
                subCategory = arguments?.getString("by_brand_cat")!!
                brand = arguments?.getString("by_brand")!!
                binding.keywordProductList.setText(subCategory)
                getProductFilter()
            }
        }

        val bottomSheet = BottomSheet()
        binding.sortByProductList.setOnClickListener {
            val data = Bundle()
            data.putString("type", "Sort By")
            if (sortByPrice != "null") {
                data.putString("inner", sortByPrice)
            }
            if (sortByTimestamp != "null") {
                data.putString("inner", "newest")
            }
            bottomSheet.arguments = data
            bottomSheet.show((activity as AppCompatActivity).supportFragmentManager, tag)
        }

        binding.priceBtnProductList.setOnClickListener {
            val data = Bundle()
            data.putString("type", "Price Range")
            bottomSheet.arguments = data
            if (minPrice != -1 && maxPrice != -1) {
                data.putString("innerPMx", maxPrice.toString())
                data.putString("innerPMi", minPrice.toString())
            }
            bottomSheet.show((activity as AppCompatActivity).supportFragmentManager, tag)
        }

        binding.brandBtnProductList.setOnClickListener {
            val data = Bundle()
            data.putString("type", "Brand Search")
            bottomSheet.arguments = data
            bottomSheet.show((activity as AppCompatActivity).supportFragmentManager, tag)
        }

        binding.filterBtnProductList.setOnClickListener {
            val bundle = Bundle()
            if (arguments?.getString("keyword") != null) {
                bundle.putString("keyword", keyword)
            }
            if (arguments?.getString("sub_Category") != null) {
                bundle.putString("sub_Category", subCategory)
            }
            if (arguments?.getString("by_brand") != null) {
                bundle.putString("by_brand", brand)
                bundle.putString("sub_Category", subCategory)
            }
            val filterPage = FilterPage()
            if (arguments?.getString("sub_Category") != null || arguments?.getString("by_brand") != null) {
                bundle.putString("from", "consumer")
                filterPage.arguments = bundle
                (activity as ConsumerDashboard?)?.navigateTo(filterPage)
            } else {
                bundle.putString("from", "searchPro")
                filterPage.arguments = bundle
                (activity as SearchProList?)?.navigateTo(filterPage)
            }
        }

        binding.ratingBtnProductList.setOnClickListener {
            val data = Bundle()
            data.putString("type", "Rating Filter")
            if (sortByRatings != "null") {
                extracted(data)
            }
            if (minRating != -1) {
                data.putString("innerRateData", minRating.toString())
            }
            bottomSheet.arguments = data
            bottomSheet.show((activity as AppCompatActivity).supportFragmentManager, tag)
        }

        binding.searchBtnProductList.setOnClickListener {
            if (binding.keywordProductList.text.isNotEmpty()) {
                keyword = binding.keywordProductList.text.toString()
                getProductFilter()
            }
        }

        binding.cartProductList.setOnClickListener {
            (activity as SearchProList?)?.finish()
        }

        binding.retryProductListBtn.setOnClickListener {

        }
        return binding.root
    }

    private fun extracted(data: Bundle) {
        data.putString("innerRateData", sortByRatings)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProductList().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun checkInternetConnection() {
        if (arguments?.getString("sub_Category") != null || arguments?.getString("by_brand") != null) {
            if ((activity as ConsumerDashboard?)?.isNetworkAvailable() == true) {
                binding.internetLyProductList.visibility = View.GONE
                binding.productListLayout.visibility = View.VISIBLE
            } else {
                binding.internetLyProductList.visibility = View.VISIBLE
                binding.productListLayout.visibility = View.GONE
            }
        } else {
            if ((activity as SearchProList?)?.isNetworkAvailable() == true) {
                binding.internetLyProductList.visibility = View.GONE
                binding.productListLayout.visibility = View.VISIBLE
            } else {
                binding.internetLyProductList.visibility = View.VISIBLE
                binding.productListLayout.visibility = View.GONE
            }
        }
    }

    private val dataReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val receivedData = intent?.getStringExtra("sortPrice")
            if (receivedData != null) {
                when (receivedData) {
                    "lowToHigh" -> {
                        brand = "null"         // Filter by brand (case-insensitive)
                        minPrice = -1             // Minimum price
                        maxPrice = -1           // Maximum price
                        minRating = -1         // Minimum rating
                        maxRating = -1          // Maximum rating
                        limit = 10               // Limit to 10 results per page
                        page = 1
                        sortByRatings = "null"
                        sortByTimestamp = "null"
                        sortByPrice = "asc"
                    }

                    "HighToLow" -> {
                        brand = "null"         // Filter by brand (case-insensitive)
                        minPrice = -1             // Minimum price
                        maxPrice = -1           // Maximum price
                        minRating = -1         // Minimum rating
                        maxRating = -1          // Maximum rating
                        limit = 10               // Limit to 10 results per page
                        page = 1
                        sortByRatings = "null"
                        sortByTimestamp = "null"
                        sortByPrice = "desc"
                    }

                    "newest" -> {
                        brand = "null"         // Filter by brand (case-insensitive)
                        minPrice = -1             // Minimum price
                        maxPrice = -1           // Maximum price
                        minRating = -1         // Minimum rating
                        maxRating = -1          // Maximum rating
                        limit = 10               // Limit to 10 results per page
                        page = 1
                        sortByPrice = "null"
                        sortByRatings = "null"
                        sortByTimestamp = "desc"
                    }

                    "relevance" -> {
                        brand = "null"         // Filter by brand (case-insensitive)
                        minPrice = -1             // Minimum price
                        maxPrice = -1           // Maximum price
                        minRating = -1         // Minimum rating
                        maxRating = -1          // Maximum rating
                        limit = 10               // Limit to 10 results per page
                        page = 1
                        sortByPrice = "null"
                        sortByRatings = "null"
                        sortByTimestamp = "null"
                    }
                }
                getProductFilter()
            }

            val priceRangeMin = intent?.getStringExtra("priceRangeMin")
            val priceRangeMax = intent?.getStringExtra("priceRangeMax")
            if (priceRangeMin != null && priceRangeMax != null) {
                minPrice = priceRangeMin.toFloat().toInt()
                maxPrice = priceRangeMax.toFloat().toInt()
                brand = "null"         // Filter by brand (case-insensitive)
                minRating = -1         // Minimum rating
                maxRating = -1          // Maximum rating
                limit = 10               // Limit to 10 results per page
                page = 1
                sortByRatings = "null"
                sortByTimestamp = "null"
                sortByPrice = "null"
                getProductFilter()
            }

            val ratingData = intent?.getStringExtra("sortRating")
            if (ratingData != null) {
                when (ratingData) {
                    "lowToHigh" -> {
                        brand = "null"         // Filter by brand (case-insensitive)
                        minPrice = -1             // Minimum price
                        maxPrice = -1           // Maximum price
                        minRating = -1         // Minimum rating
                        maxRating = -1          // Maximum rating
                        limit = 10               // Limit to 10 results per page
                        page = 1
                        sortByRatings = "asc"
                        sortByTimestamp = "null"
                        sortByPrice = "null"
                    }

                    "HighToLow" -> {
                        brand = "null"         // Filter by brand (case-insensitive)
                        minPrice = -1             // Minimum price
                        maxPrice = -1           // Maximum price
                        minRating = -1         // Minimum rating
                        maxRating = -1          // Maximum rating
                        limit = 10               // Limit to 10 results per page
                        page = 1
                        sortByRatings = "desc"
                        sortByTimestamp = "null"
                        sortByPrice = "null"
                    }

                    "relevance" -> {
                        brand = "null"         // Filter by brand (case-insensitive)
                        minPrice = -1             // Minimum price
                        maxPrice = -1           // Maximum price
                        minRating = -1         // Minimum rating
                        maxRating = -1          // Maximum rating
                        limit = 10               // Limit to 10 results per page
                        page = 1
                        sortByPrice = "null"
                        sortByRatings = "null"
                        sortByTimestamp = "null"
                    }

                    "1" -> {
                        brand = "null"         // Filter by brand (case-insensitive)
                        minPrice = -1             // Minimum price
                        maxPrice = -1           // Maximum price
                        minRating = ratingData.toInt()         // Minimum rating
                        maxRating = -1          // Maximum rating
                        limit = 10               // Limit to 10 results per page
                        page = 1
                        sortByPrice = "null"
                        sortByRatings = "null"
                        sortByTimestamp = "null"
                    }

                    "2" -> {
                        brand = "null"         // Filter by brand (case-insensitive)
                        minPrice = -1             // Minimum price
                        maxPrice = -1           // Maximum price
                        minRating = ratingData.toInt()        // Minimum rating
                        maxRating = -1       // Maximum rating
                        limit = 10               // Limit to 10 results per page
                        page = 1
                        sortByPrice = "null"
                        sortByRatings = "null"
                        sortByTimestamp = "null"
                    }

                    "3" -> {
                        brand = "null"         // Filter by brand (case-insensitive)
                        minPrice = -1             // Minimum price
                        maxPrice = -1           // Maximum price
                        minRating = ratingData.toInt()         // Minimum rating
                        maxRating = -1          // Maximum rating
                        limit = 10               // Limit to 10 results per page
                        page = 1
                        sortByPrice = "null"
                        sortByRatings = "null"
                        sortByTimestamp = "null"
                    }

                    "4" -> {
                        brand = "null"         // Filter by brand (case-insensitive)
                        minPrice = -1             // Minimum price
                        maxPrice = -1           // Maximum price
                        minRating = ratingData.toInt()         // Minimum rating
                        maxRating = -1          // Maximum rating
                        limit = 10               // Limit to 10 results per page
                        page = 1
                        sortByPrice = "null"
                        sortByRatings = "null"
                        sortByTimestamp = "null"
                    }

                    "5" -> {
                        brand = "null"         // Filter by brand (case-insensitive)
                        minPrice = -1             // Minimum price
                        maxPrice = -1           // Maximum price
                        minRating = ratingData.toInt()         // Minimum rating
                        maxRating = -1          // Maximum rating
                        limit = 10               // Limit to 10 results per page
                        page = 1
                        sortByPrice = "null"
                        sortByRatings = "null"
                        sortByTimestamp = "null"
                    }
                }
                getProductFilter()
            }

//            if (filterRating?.toInt() != -1) {
//                brand = "null"
//                minPrice = -1
//                maxPrice = -1
//                maxRating = -1
//                limit = 10
//                page = 1
//                sortByRatings = "null"
//                sortByTimestamp = "null"
//                sortByPrice = "null"
//                productCategory = "null"
//                subCategory = "null"
//                minRating = filterRating?.toInt()!!
//            }
//            if (priceMaxFil?.toInt() != -1) {
//                brand = "null"
//                minPrice = -1
//                minRating = if (filterRating.toInt() != -1) filterRating.toInt() else -1
//                maxRating = -1
//                limit = 10
//                page = 1
//                sortByRatings = "null"
//                sortByTimestamp = "null"
//                sortByPrice = "null"
//                productCategory = "null"
//                subCategory = "null"
//                maxPrice = priceMaxFil?.toInt()!!
//            }
//            if (priceMinFil?.toInt() != 150000) {
//                brand = "null"
//                maxPrice = if (priceMaxFil.toInt() != -1) priceMaxFil.toInt() else -1
//                minRating = if (filterRating.toInt() != -1) filterRating.toInt() else -1
//                maxRating = -1
//                limit = 10
//                page = 1
//                sortByRatings = "null"
//                sortByTimestamp = "null"
//                sortByPrice = "null"
//                productCategory = "null"
//                subCategory = "null"
//                minPrice = priceMinFil?.toInt()!!
//            }
//            if (filterCat != "Relevance") {
//                brand = "null"
//                minPrice = if (priceMinFil.toInt() != -1) priceMinFil.toInt() else -1
//                maxPrice = if (priceMaxFil.toInt() != -1) priceMaxFil.toInt() else -1
//                minRating = if (filterRating.toInt() != -1) filterRating.toInt() else -1
//                maxRating = -1
//                limit = 10
//                page = 1
//                sortByRatings = "null"
//                sortByTimestamp = "null"
//                sortByPrice = "null"
//                subCategory = "null"
//                productCategory = filterCat!!
//            }
//            getProductFilter()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.unregisterReceiver(dataReceiver)
    }

    private fun getProductFilter() {
        binding.productListShimmerPartnerDash.visibility = View.VISIBLE
        binding.productListShimmerPartnerDash.startShimmer()
        val sortByPrice: String? =
            if (sortByPrice == "null") null else sortByPrice        // No sorting by price
        val sortByRatings: String? =
            if (sortByRatings == "null") null else sortByRatings    // Sort by ratings descending
        val sortByTimestamp: String? =
            if (sortByTimestamp == "null") null else sortByTimestamp
        val brand: String? =
            if (brand == "null") null else brand         // Filter by brand (case-insensitive)
        val search: String? = if (keyword == "null") null else keyword
        val minPrice: Int? = if (minPrice == -1) null else minPrice               // Minimum price
        val maxPrice: Int? = if (maxPrice == -1) null else maxPrice              // Maximum price
        val minRating: Int? =
            if (minRating == -1) null else minRating                // Minimum rating
        val maxRating: Int? = if (maxRating == -1) null else maxRating           // Maximum rating
        val productCategory: String? =
            if (productCategory == "null") null else productCategory
        val subCategory: String? =
            if (subCategory == "null") null else subCategory
        val limit: Int = limit                   // Limit to 10 results per page
        val page: Int = page

        val apiService = ApiClient.getApiService()
        CoroutineScope(Dispatchers.IO).launch {
            if (apiService != null) {
                val call: Call<List<Product>> = apiService.getProductsByFilter(
                    sortByPrice,
                    sortByRatings,
                    sortByTimestamp,
                    brand,
                    search,
                    minPrice,
                    maxPrice,
                    minRating,
                    maxRating,
                    productCategory,
                    subCategory,
                    limit,
                    page
                )
                try {
                    call.enqueue(object : Callback<List<Product>> {
                        override fun onResponse(
                            call: Call<List<Product>>,
                            response: Response<List<Product>>
                        ) {
                            if (response.isSuccessful) {
                                binding.notFoundTempleateProductList.visibility = View.GONE
                                binding.productListShimmerPartnerDash.stopShimmer()
                                binding.productListShimmerPartnerDash.visibility = View.GONE
                                if (response.code() == 404) {
                                    Log.d(
                                        "FILTER_PRODUCT_ERROR",
                                        response.errorBody().toString() + "not found"
                                    )
                                    binding.notFoundTempleateProductList.visibility = View.VISIBLE
                                }
                                binding.productRecyclerviewProductList.visibility = View.VISIBLE
                                val list = response.body()
                                adapter = ProductAdapterPAcc(requireContext(), list!!)
                                binding.productRecyclerviewProductList.adapter = adapter
                            } else {
                                binding.productListShimmerPartnerDash.stopShimmer()
                                binding.notFoundTempleateProductList.visibility = View.GONE
                                binding.productListShimmerPartnerDash.visibility = View.GONE
                                binding.notFoundTempleateProductList.visibility = View.VISIBLE
                                binding.productRecyclerviewProductList.visibility = View.GONE
                                Log.d("FILTER_PRODUCT_ERROR", response.errorBody().toString())
                            }
                        }

                        override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                            Log.d("FILTER_PRODUCT_ERROR", t.message.toString())
                            binding.productListShimmerPartnerDash.stopShimmer()
                            binding.notFoundTempleateProductList.visibility = View.GONE
                            binding.productListShimmerPartnerDash.visibility = View.GONE
                            binding.productRecyclerviewProductList.visibility = View.GONE
                        }
                    })
                } catch (e: Exception) {
                    binding.productListShimmerPartnerDash.stopShimmer()
                    binding.notFoundTempleateProductList.visibility = View.GONE
                    binding.productListShimmerPartnerDash.visibility = View.GONE
                    Log.d("FILTER_PRODUCT_ERROR", e.message.toString())
                }
            }
        }
    }
}