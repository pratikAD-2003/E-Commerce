package com.pycreation.e_commerce.consumer.dashboard.tabs

import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.pycreation.e_commerce.Constants
import com.pycreation.e_commerce.R
import com.pycreation.e_commerce.consumer.dashboard.ConsumerDashboard
import com.pycreation.e_commerce.consumer.dashboard.sub_category.adaptors.SubCategoryHomeAdapter
import com.pycreation.e_commerce.consumer.dashboard.sub_category.res.SubCategoryResModel
import com.pycreation.e_commerce.consumer.dashboard.tabs.adaptors.ExploreBrandAdapter
import com.pycreation.e_commerce.databinding.FragmentExploreBinding
import com.pycreation.e_commerce.retrofit.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Explore : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentExploreBinding

    private var isPhonesDataLoaded = false
    private var isLaptopsDataLoaded = false

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
        binding = FragmentExploreBinding.inflate(inflater, container, false)
        checkInternetConnection()

        binding.retryExploreBtn.setOnClickListener {
            checkInternetConnection()
        }
        setSubCategory(
            binding.phonesRecyclerviewExplore,
            binding.phonesLayoutExplore,
            Constants.PHONES_CATEGORY,
            binding.phonesShimmerLayoutExplore
        )
        setSubCategory(
            binding.laptopsRecyclerviewExplore,
            binding.laptopsLayoutExplore,
            Constants.LAPTOPS_CATEGORY,
            binding.laptopsShimmerLayoutExplore
        )

        binding.exploreLayoutExploreFrag.viewTreeObserver.addOnScrollChangedListener {
//            if (isViewVisible(binding.phonesLayoutExplore) && !isPhonesDataLoaded) {
//                setSubCategory(
//                    binding.phonesRecyclerviewExplore,
//                    binding.phonesLayoutExplore,
//                    Constants.PHONES_CATEGORY,
//                    binding.phonesShimmerLayoutExplore
//                )
//                isPhonesDataLoaded = true
//            }
//
//            if (isViewVisible(binding.laptopsLayoutExplore) && !isLaptopsDataLoaded) {
//                setSubCategory(
//                    binding.laptopsRecyclerviewExplore,
//                    binding.laptopsLayoutExplore,
//                    Constants.LAPTOPS_CATEGORY,
//                    binding.laptopsShimmerLayoutExplore
//                )
//                isLaptopsDataLoaded = true
//            }
        }
        return binding.root;
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Explore().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun setSubCategory(
        recyclerView: RecyclerView,
        layout: CardView,
        category: String,
        shimmerFrameLayout: ShimmerFrameLayout
    ) {
        Log.d("CHECK_CATEGORY_LOADED", category)
        shimmerFrameLayout.visibility = View.VISIBLE
        shimmerFrameLayout.startShimmer()
        if (isAdded)
        recyclerView.layoutManager =
            GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)
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
                                if (response.body() != null) {
                                    if (response.body()!!.size != 0) {
                                        Handler(Looper.getMainLooper()).postDelayed({
                                            shimmerFrameLayout.visibility = View.GONE
                                            shimmerFrameLayout.stopShimmer()
                                            if (isAdded) {
                                                val adapter = ExploreBrandAdapter(
                                                    requireContext(), response.body()!!
                                                )
                                                recyclerView.adapter = adapter
                                            }
                                        }, 1000)
                                    } else {
                                        shimmerFrameLayout.visibility = View.GONE
                                        shimmerFrameLayout.stopShimmer()
                                        layout.visibility = View.GONE
                                    }
                                } else {
                                    shimmerFrameLayout.visibility = View.GONE
                                    shimmerFrameLayout.stopShimmer()
                                    layout.visibility = View.GONE
                                }
                            } else {
                                shimmerFrameLayout.visibility = View.GONE
                                shimmerFrameLayout.stopShimmer()
                                layout.visibility = View.GONE
                                Log.d("SUB_CATE_ERROR+$category", response.errorBody().toString())
                            }
                        }

                        override fun onFailure(call: Call<SubCategoryResModel?>, t: Throwable) {
                            shimmerFrameLayout.visibility = View.GONE
                            shimmerFrameLayout.stopShimmer()
                            Log.d("SUB_CATE_ERROR+$category", t.message.toString())
                            layout.visibility = View.GONE
                        }

                    })
            } catch (e: Exception) {
                shimmerFrameLayout.visibility = View.GONE
                shimmerFrameLayout.stopShimmer()
                Log.d("SUB_CATE_ERROR+$category", e.message.toString())
                layout.visibility = View.GONE
            }
        }
    }

    private fun checkInternetConnection() {
        if ((activity as ConsumerDashboard?)?.isNetworkAvailable() == true) {
            binding.internetLyExplore.visibility = View.GONE
            binding.exploreLayoutExploreFrag.visibility = View.VISIBLE
        } else {
            binding.internetLyExplore.visibility = View.VISIBLE
            binding.exploreLayoutExploreFrag.visibility = View.GONE
        }
    }

    private fun isViewVisible(view: View): Boolean {
        val scrollBounds = Rect()
        binding.exploreLayoutExploreFrag.getHitRect(scrollBounds)
        return view.getLocalVisibleRect(scrollBounds)
    }
}