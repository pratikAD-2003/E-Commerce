package com.pycreation.e_commerce.consumer.wishlist.activities

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.pycreation.e_commerce.R
import com.pycreation.e_commerce.UserSharedPref
import com.pycreation.e_commerce.common.productdetails.ProductLifeCycle
import com.pycreation.e_commerce.consumer.addToCart.request.AddToCartResModel
import com.pycreation.e_commerce.consumer.dashboard.ConsumerDashboard
import com.pycreation.e_commerce.consumer.responseModels.RegisterResponse
import com.pycreation.e_commerce.consumer.wishlist.adapters.WishlistItemAdapter
import com.pycreation.e_commerce.consumer.wishlist.models.request.AddToWishListRequestModel
import com.pycreation.e_commerce.consumer.wishlist.models.response.WishListItemResModel
import com.pycreation.e_commerce.databinding.FragmentWishListProductsBinding
import com.pycreation.e_commerce.retrofit.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class WishListProductsFrag : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentWishListProductsBinding
    private lateinit var adapter: WishlistItemAdapter

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
        binding = FragmentWishListProductsBinding.inflate(inflater, container, false)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().supportFragmentManager.popBackStack()
        }
        setupActionBar()
        binding.wishListItemsRecyclerviewWishListProd.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        getWishListItems()
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            WishListProductsFrag().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun setupActionBar() {
        (activity as AppCompatActivity).setSupportActionBar(binding.myWishlistToolbar)
        val actionBar = (activity as AppCompatActivity).supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
        }
        binding.myWishlistToolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun addToCartProduct(productUid: String) {
        (activity as ConsumerDashboard?)?.showDialog("Add to Cart...")
        val apiService = ApiClient.getApiService()
        val addToCartResModel =
            AddToCartResModel(productUid, 1, UserSharedPref(requireContext()).getToken())
        CoroutineScope(Dispatchers.IO).launch {
            try {
                apiService?.addToCartProduct(addToCartResModel)
                    ?.enqueue(object : Callback<RegisterResponse?> {
                        override fun onResponse(
                            call: Call<RegisterResponse?>,
                            response: Response<RegisterResponse?>
                        ) {
                            if (response.isSuccessful) {
                                (activity as ConsumerDashboard?)?.dismissDialog()
                                (activity as ConsumerDashboard?)?.showError(response.body()!!.message)
                                Log.d("ADD_TO_CART_PRODUCT", response.body()!!.message)
                                setupSaveToWishlistProduct(productUid)
                            }
                        }

                        override fun onFailure(call: Call<RegisterResponse?>, t: Throwable) {
                            (activity as ConsumerDashboard?)?.dismissDialog()
                            Log.d("ADD_TO_CART_PRODUCT", t.message.toString())
                        }
                    })

            } catch (e: Exception) {
                (activity as ConsumerDashboard?)?.dismissDialog()
                Log.d("ADD_TO_CART_PRODUCT", e.message.toString())
            }
        }
    }

    private fun setupSaveToWishlistProduct(productUid: String) {
        (activity as ConsumerDashboard?)?.showDialog("Wait...")
        val apiService = ApiClient.getApiService()
        val addToWishListRequestModel =
            AddToWishListRequestModel(productUid, UserSharedPref(requireContext()).getToken())
        CoroutineScope(Dispatchers.IO).launch {
            try {
                apiService?.addToWishListProduct(addToWishListRequestModel)
                    ?.enqueue(object : Callback<RegisterResponse?> {
                        override fun onResponse(
                            call: Call<RegisterResponse?>,
                            response: Response<RegisterResponse?>
                        ) {
                            if (response.isSuccessful) {
                                (activity as ConsumerDashboard?)?.dismissDialog()
                                (activity as ConsumerDashboard?)?.showError(response.body()!!.message)
                                Log.d("WISH_LIST_REMOVE_ADD_WISHLIST", response.body()!!.message)
                                getWishListItems()
                            }
                        }

                        override fun onFailure(call: Call<RegisterResponse?>, t: Throwable) {
                            (activity as ConsumerDashboard?)?.dismissDialog()
                            Log.d("WISH_LIST_REMOVE_ADD_WISHLIST", t.message.toString())
                        }
                    })

            } catch (e: Exception) {
                (activity as ConsumerDashboard?)?.dismissDialog()
                Log.d("WISH_LIST_REMOVE_ADD_WISHLIST", e.message.toString())
            }
        }
    }

    private fun getWishListItems() {
        binding.wishListItemShimmerWishListProd.visibility = View.VISIBLE
        binding.wishListItemShimmerWishListProd.startShimmer()
        val apiService = ApiClient.getApiService()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                apiService?.getWishListProducts(UserSharedPref(requireContext()).getToken())
                    ?.enqueue(object : Callback<WishListItemResModel?> {
                        override fun onResponse(
                            call: Call<WishListItemResModel?>,
                            response: Response<WishListItemResModel?>
                        ) {
                            if (response.isSuccessful) {
                                binding.wishListItemShimmerWishListProd.visibility = View.GONE
                                binding.wishListItemShimmerWishListProd.stopShimmer()
                                if (response.body() != null) {
                                    if (response.body()!!.wishlist.isEmpty()) {
                                        binding.emptyWishListLayoutWishListPro.visibility =
                                            View.VISIBLE
                                    }
                                }
                                adapter = WishlistItemAdapter(requireContext(),
                                    response.body()!!.wishlist,
                                    onAddToCart = {
                                        addToCartProduct(it)
                                    },
                                    onRemoveItem = {
                                        setupSaveToWishlistProduct(it)
                                    })

                                binding.wishListItemsRecyclerviewWishListProd.adapter = adapter
                            } else {
                                binding.wishListItemShimmerWishListProd.visibility = View.GONE
                                binding.wishListItemShimmerWishListProd.stopShimmer()
                                Log.d(
                                    "ERROR_WHILE_WISHLIST_GET_IDs",
                                    response.errorBody().toString()
                                )
                            }
                        }

                        override fun onFailure(
                            call: Call<WishListItemResModel?>,
                            t: Throwable
                        ) {
                            binding.wishListItemShimmerWishListProd.visibility = View.GONE
                            binding.wishListItemShimmerWishListProd.stopShimmer()
                            Log.d("ERROR_WHILE_WISHLIST_GET_IDs", t.message.toString())
                        }

                    })
            } catch (e: Exception) {
                binding.wishListItemShimmerWishListProd.visibility = View.GONE
                binding.wishListItemShimmerWishListProd.stopShimmer()
                Log.d("ERROR_WHILE_WISHLIST_GET_IDs", e.message.toString())
            }
        }
    }
}