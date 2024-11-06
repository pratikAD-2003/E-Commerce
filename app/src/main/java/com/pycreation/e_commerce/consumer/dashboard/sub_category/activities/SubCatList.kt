package com.pycreation.e_commerce.consumer.dashboard.sub_category.activities

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pycreation.e_commerce.Constants
import com.pycreation.e_commerce.R
import com.pycreation.e_commerce.consumer.dashboard.ConsumerDashboard
import com.pycreation.e_commerce.consumer.dashboard.sub_category.adaptors.SubCategoryHomeAdapter
import com.pycreation.e_commerce.consumer.dashboard.sub_category.res.SubCategoryResModel
import com.pycreation.e_commerce.databinding.FragmentSubCatListBinding
import com.pycreation.e_commerce.retrofit.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SubCatList : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentSubCatListBinding

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
        binding = FragmentSubCatListBinding.inflate(inflater, container, false)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().supportFragmentManager.popBackStack()
        }
        checkInternetConnection()
        setupActionBar()
        val category = arguments?.getString("subCategory")
        setUpSubCategory(category!!)

        binding.retrySubCatListBtn.setOnClickListener {

        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SubCatList().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun checkInternetConnection() {
        if ((activity as ConsumerDashboard?)?.isNetworkAvailable() == true) {
            binding.internetLySubCatList.visibility = View.GONE
            binding.subCatListLayout.visibility = View.VISIBLE
        } else {
            binding.internetLySubCatList.visibility = View.VISIBLE
            binding.subCatListLayout.visibility = View.GONE
        }
    }


    private fun setUpSubCategory(category: String) {
        var cat: String = "null"
        when (category) {
            "1" -> {
                cat = "Electronics"
                (activity as AppCompatActivity).supportActionBar?.title = "Best Of Electronics"
            }

            "2" -> {
                cat = "Fashion"
                (activity as AppCompatActivity).supportActionBar?.title = "Trending Fashion"
            }

            "3" -> {
                cat = "Home & Furniture"
                (activity as AppCompatActivity).supportActionBar?.title = "Home & Furniture"
            }

            "4" -> {
                cat = "Beauty & Personal Care"
                (activity as AppCompatActivity).supportActionBar?.title = "Beauty & Personal Care"
            }

            "5" -> {
                cat = "Books & Stationery"
                (activity as AppCompatActivity).supportActionBar?.title = "Books & Stationery"
            }

            "6" -> {
                cat = "Sports,Fitness & Outdoors"
                (activity as AppCompatActivity).supportActionBar?.title =
                    "Sports,Fitness & Outdoors"
            }

            "7" -> {
                cat = "Toys,Kids & Baby Products"
                (activity as AppCompatActivity).supportActionBar?.title =
                    "Toys,Kids & Baby Products"
            }

            "12" -> {
                cat = "Travel & Luggage"
                (activity as AppCompatActivity).supportActionBar?.title = "Travel & Luggage"
            }

            "11" -> {
                cat = "Gaming"
                (activity as AppCompatActivity).supportActionBar?.title = "Beast Gaming"
            }

            "8" -> {
                cat = "Groceries & Essentials"
                (activity as AppCompatActivity).supportActionBar?.title = "Groceries & Essentials"
            }

            "10" -> {
                cat = "Health & Nutrition"
                (activity as AppCompatActivity).supportActionBar?.title = "Health & Nutrition"
            }

            "14" -> {
                cat = "Jewelry"
                (activity as AppCompatActivity).supportActionBar?.title = "Jewelry"
            }
            "13" -> {
                cat = "Pet Supplies"
                (activity as AppCompatActivity).supportActionBar?.title = "Pet Supplies"
            }
            "9" -> {
                cat = "Automotive"
                (activity as AppCompatActivity).supportActionBar?.title = "Automotive"
            }
        }
        if (cat != "null") {
            setSubCategory(binding.subCatListRecyclerview, cat)
        }
    }

    private fun setSubCategory(recyclerView: RecyclerView, category: String) {
        binding.subCateShimmerLayoutSubCatList.visibility = View.VISIBLE
        binding.subCateShimmerLayoutSubCatList.startShimmer()
        recyclerView.layoutManager =
            GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
        recyclerView.setHasFixedSize(true)
        recyclerView.setItemViewCacheSize(4)
        val apiService = ApiClient.getApiService()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                apiService?.getSubCategories(category)
                    ?.enqueue(object : Callback<SubCategoryResModel?> {
                        override fun onResponse(
                            call: Call<SubCategoryResModel?>,
                            response: Response<SubCategoryResModel?>
                        ) {
                            if (isAdded && response.isSuccessful) {
                                binding.subCateShimmerLayoutSubCatList.visibility = View.GONE
                                binding.subCateShimmerLayoutSubCatList.stopShimmer()
                                Log.d("SUB_CATE_ERROR+$category", response.body().toString())
                                if (response.body() != null) {
                                    val adapter =
                                        SubCategoryHomeAdapter(
                                            requireContext(),
                                            response.body()!!
                                        )
                                    recyclerView.adapter = adapter
                                }
                            } else {
                                binding.subCateShimmerLayoutSubCatList.visibility = View.GONE
                                binding.subCateShimmerLayoutSubCatList.stopShimmer()
                                Log.d("SUB_CATE_ERROR+$category", response.errorBody().toString())
                            }
                        }

                        override fun onFailure(call: Call<SubCategoryResModel?>, t: Throwable) {
                            binding.subCateShimmerLayoutSubCatList.visibility = View.GONE
                            binding.subCateShimmerLayoutSubCatList.stopShimmer()
                            Log.d("SUB_CATE_ERROR+$category", t.message.toString())
                        }

                    })
            } catch (e: Exception) {
                binding.subCateShimmerLayoutSubCatList.visibility = View.GONE
                binding.subCateShimmerLayoutSubCatList.stopShimmer()
                Log.d("SUB_CATE_ERROR+$category", e.message.toString())
            }
        }
    }

    private fun setupActionBar() {
        (activity as AppCompatActivity).setSupportActionBar(binding.subCateListToolbar)
        val actionBar = (activity as AppCompatActivity).supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
        }
        binding.subCateListToolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }
}