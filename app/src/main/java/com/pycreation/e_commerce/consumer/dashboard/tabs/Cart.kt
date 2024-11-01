package com.pycreation.e_commerce.consumer.dashboard.tabs

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.pycreation.e_commerce.R
import com.pycreation.e_commerce.UserSharedPref
import com.pycreation.e_commerce.admin.listening.adapter.ProductAdapterPAcc
import com.pycreation.e_commerce.admin.models.productModel.Product
import com.pycreation.e_commerce.common.productdetails.ProductLifeCycle
import com.pycreation.e_commerce.consumer.addToCart.activities.PlacedOrder
import com.pycreation.e_commerce.consumer.addToCart.response.cartresmodel.CartItemResModel
import com.pycreation.e_commerce.consumer.dashboard.ConsumerDashboard
import com.pycreation.e_commerce.consumer.dashboard.adaptors.CartProductAdapter
import com.pycreation.e_commerce.consumer.responseModels.RegisterResponse
import com.pycreation.e_commerce.consumer.wishlist.models.request.AddToWishListRequestModel
import com.pycreation.e_commerce.databinding.FragmentCartBinding
import com.pycreation.e_commerce.retrofit.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Cart : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentCartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onStart() {
        super.onStart()
        binding.proceedToCheckoutBtnCartFrag.visibility = View.GONE
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)

        binding.cartRecyclerviewCartFrag.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        getCartProducts()

        binding.proceedToCheckoutBtnCartFrag.setOnClickListener {
            (activity as ConsumerDashboard?)?.navigateTo(PlacedOrder())
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Cart().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun increaseQuantity(productUid: String) {
        (activity as ConsumerDashboard?)?.showDialog("Wait...")
        val apiService = ApiClient.getApiService()
        val addToWishListRequestModel =
            AddToWishListRequestModel(productUid, UserSharedPref(requireContext()).getToken())
        CoroutineScope(Dispatchers.IO).launch {
            try {
                apiService?.increaseQuantity(addToWishListRequestModel)
                    ?.enqueue(object : Callback<RegisterResponse?> {
                        override fun onResponse(
                            call: Call<RegisterResponse?>,
                            response: Response<RegisterResponse?>
                        ) {
                            if (response.isSuccessful) {
                                (activity as ConsumerDashboard?)?.dismissDialog()
//                                (activity as ConsumerDashboard?)?.showError(response.body()!!.message)
                                Log.d("INCREASE_QUANTITY", response.body()!!.message)
                                getCartProducts()
                            }
                        }

                        override fun onFailure(call: Call<RegisterResponse?>, t: Throwable) {
                            (activity as ConsumerDashboard?)?.dismissDialog()
                            Log.d("INCREASE_QUANTITY", t.message.toString())
                        }

                    })

            } catch (e: Exception) {
                (activity as ConsumerDashboard?)?.dismissDialog()
                Log.d("INCREASE_QUANTITY", e.message.toString())
            }
        }
    }

    private fun decreaseQuantity(productUid: String) {
        (activity as ConsumerDashboard?)?.showDialog("Wait...")
        val apiService = ApiClient.getApiService()
        val addToWishListRequestModel =
            AddToWishListRequestModel(productUid, UserSharedPref(requireContext()).getToken())
        CoroutineScope(Dispatchers.IO).launch {
            try {
                apiService?.decreaseQuantity(addToWishListRequestModel)
                    ?.enqueue(object : Callback<RegisterResponse?> {
                        override fun onResponse(
                            call: Call<RegisterResponse?>,
                            response: Response<RegisterResponse?>
                        ) {
                            if (response.isSuccessful) {
                                (activity as ConsumerDashboard?)?.dismissDialog()
//                                (activity as ConsumerDashboard?)?.showError(response.body()!!.message)
                                Log.d("DECREASE_QUANTITY", response.body()!!.message)
                                getCartProducts()
                            }
                        }

                        override fun onFailure(call: Call<RegisterResponse?>, t: Throwable) {
                            (activity as ConsumerDashboard?)?.dismissDialog()
                            Log.d("DECREASE_QUANTITY", t.message.toString())
                        }

                    })

            } catch (e: Exception) {
                (activity as ConsumerDashboard?)?.dismissDialog()
                Log.d("DECREASE_QUANTITY", e.message.toString())
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
                                Log.d("SAVE_TO_WISH_PRODUCT_DETAIL", response.body()!!.message)
                                removeFromCart(productUid)
                            }
                        }

                        override fun onFailure(call: Call<RegisterResponse?>, t: Throwable) {
                            (activity as ConsumerDashboard?)?.dismissDialog()
                            Log.d("SAVE_TO_WISH_PRODUCT_DETAIL", t.message.toString())
                        }

                    })

            } catch (e: Exception) {
                (activity as ConsumerDashboard?)?.dismissDialog()
                Log.d("SAVE_TO_WISH_PRODUCT_DETAIL", e.message.toString())
            }
        }
    }

    private fun getCartProducts() {
        binding.cartProductShimmerCartFrag.visibility = View.VISIBLE
        binding.cartProductShimmerCartFrag.startShimmer()
        val apiService = ApiClient.getApiService()
        val productList = ArrayList<Product>()
        val quantityList = ArrayList<String>()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                apiService?.getCartProducts(UserSharedPref(requireContext()).getToken())
                    ?.enqueue(object : Callback<CartItemResModel?> {
                        override fun onResponse(
                            call: Call<CartItemResModel?>,
                            response: Response<CartItemResModel?>
                        ) {
                            if (response.isSuccessful) {
                                binding.cartProductShimmerCartFrag.visibility = View.GONE
                                binding.cartProductShimmerCartFrag.stopShimmer()
                                response.body()?.let { cartItemResModel ->
                                    if (cartItemResModel.cart.isEmpty()) {
                                        binding.proceedToCheckoutBtnCartFrag.visibility = View.GONE
                                    } else {
                                        binding.proceedToCheckoutBtnCartFrag.visibility =
                                            View.VISIBLE
                                    }
                                    // Check if the cart is not null before iterating
                                    cartItemResModel.cart.forEach { cart ->
                                        quantityList.add(cart.quantity.toString())
                                        cart.productDetails.let { product ->
                                            productList.add(product)
                                        }
                                    }
                                }
                                val adapter = CartProductAdapter(
                                    requireContext(),
                                    productList,
                                    quantityList,
                                    onRemoveFromCart = {
                                        removeFromCart(it)
                                    },
                                    onMoveToWishlist = {
                                        setupSaveToWishlistProduct(it)
                                    },
                                    onIncrease = {
                                        increaseQuantity(it)
                                    },
                                    onDecrease = {
                                        decreaseQuantity(it)
                                    })
                                binding.cartRecyclerviewCartFrag.adapter = adapter
                                Log.d("CART_PRODUCT_ITEMS", response.body().toString())
                            } else {
                                binding.proceedToCheckoutBtnCartFrag.visibility = View.GONE
                                binding.cartProductShimmerCartFrag.visibility = View.GONE
                                binding.cartProductShimmerCartFrag.stopShimmer()
                                Log.d("CART_PRODUCT_ITEMS", response.errorBody().toString())
                            }
                        }

                        override fun onFailure(call: Call<CartItemResModel?>, t: Throwable) {
                            binding.proceedToCheckoutBtnCartFrag.visibility = View.GONE
                            binding.cartProductShimmerCartFrag.visibility = View.GONE
                            binding.cartProductShimmerCartFrag.stopShimmer()
                            Log.d("CART_PRODUCT_ITEMS", t.message.toString())
                        }
                    })
            } catch (e: Exception) {
                binding.proceedToCheckoutBtnCartFrag.visibility = View.GONE
                binding.cartProductShimmerCartFrag.visibility = View.GONE
                binding.cartProductShimmerCartFrag.stopShimmer()
                Log.d("CART_PRODUCT_ITEMS", e.message.toString())
            }
        }
    }

    private fun removeFromCart(productUid: String) {
        (activity as ConsumerDashboard?)?.showDialog("Wait...")
        val apiService = ApiClient.getApiService()
        val addToWishListRequestModel =
            AddToWishListRequestModel(productUid, UserSharedPref(requireContext()).getToken())
        CoroutineScope(Dispatchers.IO).launch {
            try {
                apiService?.deleteCartProduct(addToWishListRequestModel)
                    ?.enqueue(object : Callback<RegisterResponse?> {
                        override fun onResponse(
                            call: Call<RegisterResponse?>,
                            response: Response<RegisterResponse?>
                        ) {
                            if (response.isSuccessful) {
                                (activity as ConsumerDashboard?)?.dismissDialog()
                                (activity as ConsumerDashboard?)?.showError(response.body()!!.message)
                                Log.d("REMOVE_FROM_CART", response.body().toString())
                                getCartProducts()
                            } else {
                                Log.d("REMOVE_FROM_CART", response.errorBody().toString())
                                (activity as ConsumerDashboard?)?.dismissDialog()
                            }
                        }

                        override fun onFailure(call: Call<RegisterResponse?>, t: Throwable) {
                            Log.d("REMOVE_FROM_CART", t.message.toString())
                            (activity as ConsumerDashboard?)?.dismissDialog()
                        }
                    })
            } catch (e: Error) {
                Log.d("REMOVE_FROM_CART", e.message.toString())
                (activity as ConsumerDashboard?)?.dismissDialog()
            }
        }
    }
}