package com.pycreation.e_commerce.consumer.addToCart.activities

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
import com.pycreation.e_commerce.admin.models.productModel.Product
import com.pycreation.e_commerce.consumer.addToCart.adaptors.PlacedProductAdapter
import com.pycreation.e_commerce.consumer.addToCart.response.cartresmodel.CartItemResModel
import com.pycreation.e_commerce.consumer.address.activities.AddressList
import com.pycreation.e_commerce.consumer.address.adaptors.AddressAdapter
import com.pycreation.e_commerce.consumer.address.model.res.AddressListModelResModel
import com.pycreation.e_commerce.consumer.dashboard.ConsumerDashboard
import com.pycreation.e_commerce.consumer.dashboard.adaptors.CartProductAdapter
import com.pycreation.e_commerce.consumer.responseModels.RegisterResponse
import com.pycreation.e_commerce.consumer.wishlist.models.request.AddToWishListRequestModel
import com.pycreation.e_commerce.databinding.FragmentPlacedOrderBinding
import com.pycreation.e_commerce.retrofit.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class PlacedOrder : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentPlacedOrderBinding
    private lateinit var totalAmount: String
    private var checkCodBooleanList = ArrayList<Boolean>()

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
        binding = FragmentPlacedOrderBinding.inflate(inflater, container, false)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().supportFragmentManager.popBackStack()
        }
        setupActionBar()
        checkInternetConnection()
        binding.cartProductsRecyclerviewPlacedOrder.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        getAddressList()
        getCartProducts()

        binding.addAddressPlacedOrder.setOnClickListener {
            (activity as ConsumerDashboard?)?.navigateTo(AddressList())
        }

        binding.changeAddressBtnAddresses.setOnClickListener {
            (activity as ConsumerDashboard?)?.navigateTo(AddressList())
        }

        binding.retryPlacedOrderBtn.setOnClickListener {

        }

        binding.checkoutPlacedOrder.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("amount", totalAmount)
            bundle.putString("cod", checkCodBooleanList.all { it }.toString())
            bundle.putString(
                "address",
                binding.fullNamePlacedOrder.text.toString() + " (" + binding.addressTypePlacedOrder.text.toString() + ") " + binding.numberPlacedOrder.text.toString() + " " + binding.fullAddressPlacedOrder.text.toString()
            )
            val checkoutFrag = CheckoutPayment()
            checkoutFrag.arguments = bundle
            (activity as ConsumerDashboard?)?.navigateTo(checkoutFrag)
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PlacedOrder().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun checkInternetConnection() {
        if ((activity as ConsumerDashboard?)?.isNetworkAvailable() == true) {
            binding.internetLyPlacedOrderList.visibility = View.GONE
            binding.placedOrderLayout.visibility = View.VISIBLE
        } else {
            binding.internetLyPlacedOrderList.visibility = View.VISIBLE
            binding.placedOrderLayout.visibility = View.GONE
        }
    }

    private fun setupActionBar() {
        (activity as AppCompatActivity).setSupportActionBar(binding.placedOrderToolbar)
        val actionBar = (activity as AppCompatActivity).supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
        }
        binding.placedOrderToolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }


    private fun getAddressList() {
        val apiService = ApiClient.getApiService()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                apiService?.getAddresses(UserSharedPref(requireContext()).getToken())
                    ?.enqueue(object : Callback<AddressListModelResModel?> {
                        override fun onResponse(
                            call: Call<AddressListModelResModel?>,
                            response: Response<AddressListModelResModel?>
                        ) {
                            if (response.isSuccessful) {
                                if (response.body() != null && response.body()!!.isNotEmpty()) {
                                    binding.selectedAddressLayoutPlacedOrder.visibility =
                                        View.VISIBLE
                                    binding.changeAddressBtnAddresses.visibility = View.VISIBLE
                                    binding.addAddressPlacedOrder.visibility = View.GONE
                                    binding.fullNamePlacedOrder.text = response.body()!![0].fullName
                                    binding.addressTypePlacedOrder.text = response.body()!![0].type
                                    binding.numberPlacedOrder.text =
                                        response.body()!![0].phoneNumber.toString()
                                    binding.fullAddressPlacedOrder.text =
                                        response.body()!![0].area + ", " + response.body()!![0].city + ", " + response.body()!![0].pinCode + ", " + response.body()!![0].state
                                } else {
                                    binding.selectedAddressLayoutPlacedOrder.visibility = View.GONE
                                    binding.addAddressPlacedOrder.visibility = View.VISIBLE
                                    binding.changeAddressBtnAddresses.visibility = View.GONE
                                }
                                Log.d("ADDRESS_LIST", response.body().toString())
                            } else {
                                Log.d("ADDRESS_LIST", response.errorBody().toString())
                                binding.selectedAddressLayoutPlacedOrder.visibility = View.GONE
                                binding.changeAddressBtnAddresses.visibility = View.GONE
                                binding.addAddressPlacedOrder.visibility = View.VISIBLE
                            }
                        }

                        override fun onFailure(
                            call: Call<AddressListModelResModel?>,
                            t: Throwable
                        ) {
                            Log.d("ADDRESS_LIST", t.message.toString())
                            binding.selectedAddressLayoutPlacedOrder.visibility = View.GONE
                            binding.changeAddressBtnAddresses.visibility = View.GONE
                            binding.addAddressPlacedOrder.visibility = View.VISIBLE
                        }
                    })
            } catch (e: Exception) {
                Log.d("ADDRESS_LIST", e.message.toString())
                binding.selectedAddressLayoutPlacedOrder.visibility = View.GONE
                binding.changeAddressBtnAddresses.visibility = View.GONE
                binding.addAddressPlacedOrder.visibility = View.VISIBLE
            }
        }
    }

    private fun getCartProducts() {
//        binding.cartProductShimmerCartFrag.visibility = View.VISIBLE
//        binding.cartProductShimmerCartFrag.startShimmer()
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
                            if (isAdded && response.isSuccessful) {
//                                binding.cartProductShimmerCartFrag.visibility = View.GONE
//                                binding.cartProductShimmerCartFrag.stopShimmer()
                                response.body()?.let { cartItemResModel ->
                                    // Check if the cart is not null before iterating
                                    cartItemResModel.cart.forEach { cart ->
                                        quantityList.add(cart.quantity.toString())
                                        cart.productDetails.let { product ->
                                            productList.add(product)
                                            checkCodBooleanList.add(product.cashOnDelivery)
                                        }
                                    }
                                }
                                if (response.body() != null) {
                                    val shippingCost = response.body()!!.totalShippingCost
                                    val platformFee = response.body()!!.platform
                                    val totalPrice = response.body()!!.totalPrice
                                    totalAmount =
                                        (totalPrice + shippingCost + platformFee).toInt().toString()
                                    if (shippingCost == 0) {
                                        binding.deliveryChargesPlacedOrder.text = "Free Delivery"
                                    } else {
                                        binding.deliveryChargesPlacedOrder.text =
                                            "₹" + formatNumberWithCommas(shippingCost)
                                    }
                                    binding.platformFeePlacedOrder.text =
                                        "₹" + formatNumberWithCommas(platformFee.toInt()) + "(2%)"
                                    binding.subTotalPlacedOrder.text =
                                        "₹" + formatNumberWithCommas((totalPrice).toInt())
                                    binding.totalPlacedOrder.text =
                                        "₹" + formatNumberWithCommas((totalPrice + shippingCost + platformFee).toInt())
                                }
                                val adapter = PlacedProductAdapter(
                                    requireContext(),
                                    productList,
                                    quantityList,
                                    onIncrease = {
                                        increaseQuantity(it)
                                    },
                                    onDecrease = {
                                        decreaseQuantity(it)
                                    })
                                binding.cartProductsRecyclerviewPlacedOrder.adapter = adapter
                                Log.d("CART_PRODUCT_ITEMS", response.body().toString())
                            } else {
//                                binding.cartProductShimmerCartFrag.visibility = View.GONE
//                                binding.cartProductShimmerCartFrag.stopShimmer()
                                Log.d("CART_PRODUCT_ITEMS", response.errorBody().toString())
                            }
                        }

                        override fun onFailure(call: Call<CartItemResModel?>, t: Throwable) {
//                            binding.cartProductShimmerCartFrag.visibility = View.GONE
//                            binding.cartProductShimmerCartFrag.stopShimmer()
                            Log.d("CART_PRODUCT_ITEMS", t.message.toString())
                        }
                    })
            } catch (e: Exception) {
//                binding.cartProductShimmerCartFrag.visibility = View.GONE
//                binding.cartProductShimmerCartFrag.stopShimmer()
                Log.d("CART_PRODUCT_ITEMS", e.message.toString())
            }
        }
    }

    private fun formatNumberWithCommas(number: Int): String {
        val formatter = NumberFormat.getInstance(Locale("en", "IN"))
        return formatter.format(number)
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
}