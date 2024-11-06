package com.pycreation.e_commerce.common.productdetails

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.gson.Gson
import com.pycreation.e_commerce.R
import com.pycreation.e_commerce.UserSharedPref
import com.pycreation.e_commerce.admin.listening.adapter.ProductHoriAdapter
import com.pycreation.e_commerce.admin.models.productModel.Product
import com.pycreation.e_commerce.admin.models.productModel.ProductModel
import com.pycreation.e_commerce.admin.models.productSpecifications.ProductSpecificationsItem
import com.pycreation.e_commerce.common.ARG_PARAM1
import com.pycreation.e_commerce.common.ARG_PARAM2
import com.pycreation.e_commerce.common.adaptors.ImageSliderAdapter
import com.pycreation.e_commerce.common.reviews.adaptors.ReviewAdapter
import com.pycreation.e_commerce.common.reviews.models.ReviewModelItem
import com.pycreation.e_commerce.consumer.addToCart.request.AddToCartResModel
import com.pycreation.e_commerce.consumer.dashboard.ConsumerDashboard
import com.pycreation.e_commerce.consumer.dashboard.tabs.Cart
import com.pycreation.e_commerce.consumer.responseModels.RegisterResponse
import com.pycreation.e_commerce.consumer.wishlist.models.request.AddToWishListRequestModel
import com.pycreation.e_commerce.databinding.FragmentProductDetailedBinding
import com.pycreation.e_commerce.retrofit.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs

class ProductDetailed : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentProductDetailedBinding
    private lateinit var productDetail: Product
    private lateinit var productUid: String
    private var isAlreadyInCart = false

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
        binding = FragmentProductDetailedBinding.inflate(inflater, container, false)

        val gson = Gson()
        productDetail = gson.fromJson(arguments?.getString("data"), Product::class.java)
        productUid = productDetail.productUid
        checkAlreadySavedProduct()
        checkAlreadyProductInCart()
        setProductData()
        getProductReviews(productDetail.productUid)
        getSpecificationsById(productDetail.productUid)
        getBySellerID(productDetail.sellerUid)

        binding.saveBtnProDe.setOnClickListener {
            setupSaveToWishlistProduct()
        }

        binding.shareProBtnProDe.setOnClickListener {

        }

        binding.addToCartProDe.setOnClickListener {
            if (isAlreadyInCart) {
                (activity as ProductLifeCycle?)?.startActivity(
                    Intent(
                        requireContext(),
                        ConsumerDashboard::class.java
                    )
                )
                (activity as ProductLifeCycle?)?.finish()
//                (activity as ConsumerDashboard?)?.navigateTo(Cart())
//                (activity as ConsumerDashboard?)?.setCartIcon()
            } else {
                addToCartProduct(productDetail.productUid)
            }
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProductDetailed().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun setProductData() {
        setImageAdapter(
            productDetail.productImages
        )
        setRatings(productDetail.ratings)
        binding.titleProDe.text = productDetail.productName
        binding.ratingCountItemProDe.text = productDetail.ratings.toString()
        binding.reviewCountItemProDe.text = "(" + productDetail.reviewsCount + ")"
        binding.orgPriceItemProDe.text =
            "₹" + formatNumberWithCommas(productDetail.productPrice.toInt())
        binding.sellingPriceItemProDe.text =
            "₹" + formatNumberWithCommas(productDetail.productSellingPrice.toInt())
        binding.dropDownPercentageItem.text = calculatePriceDropPercentage(
            productDetail.productPrice.toInt(),
            productDetail.productSellingPrice.toInt()
        ).toString() + "% Price drop"
        if (!productDetail.freeDelivery) {
            binding.freeDeliveryTextProDe.text =
                "Delivery Charges - ₹" + productDetail.deliveryCharges.toString()
        }
        if (!productDetail.cashOnDelivery) {
            binding.codLyProDe.visibility = View.INVISIBLE
        }
        binding.brandNameProDe.text = productDetail.brand
        binding.warrantyProDe.text = productDetail.warranty
        binding.descriptionProDe.text = productDetail.productDescription
    }

    private fun addToCartProduct(productUid: String) {
        (activity as ProductLifeCycle?)?.showDialog("Add to Cart...")
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
                                (activity as ProductLifeCycle?)?.dismissDialog()
                                (activity as ProductLifeCycle?)?.showError(response.body()!!.message)
                                isAlreadyInCart = true
                                checkAlreadyProductInCart()
                                Log.d("ADD_TO_CART_PRODUCT_DETAILED", response.body()!!.message)
                            }
                        }

                        override fun onFailure(call: Call<RegisterResponse?>, t: Throwable) {
                            (activity as ProductLifeCycle?)?.dismissDialog()
                            Log.d("ADD_TO_CART_PRODUCT_DETAILED", t.message.toString())
                        }
                    })

            } catch (e: Exception) {
                (activity as ProductLifeCycle?)?.dismissDialog()
                Log.d("ADD_TO_CART_PRODUCT_DETAILED", e.message.toString())
            }
        }
    }

    private fun setupSaveToWishlistProduct() {
        (activity as ProductLifeCycle?)?.showDialog("Wait...")
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
                                (activity as ProductLifeCycle?)?.dismissDialog()
                                (activity as ProductLifeCycle?)?.showError(response.body()!!.message)
                                Log.d("SAVE_TO_WISH_PRODUCT_DETAIL", response.body()!!.message)
                                checkAlreadySavedProduct()
                            }
                        }

                        override fun onFailure(call: Call<RegisterResponse?>, t: Throwable) {
                            (activity as ProductLifeCycle?)?.dismissDialog()
                            Log.d("SAVE_TO_WISH_PRODUCT_DETAIL", t.message.toString())
                        }

                    })

            } catch (e: Exception) {
                (activity as ProductLifeCycle?)?.dismissDialog()
                Log.d("SAVE_TO_WISH_PRODUCT_DETAIL", e.message.toString())
            }
        }
    }


    private fun checkAlreadySavedProduct() {
        val apiService = ApiClient.getApiService()
        val addToWishListRequestModel =
            AddToWishListRequestModel(productUid, UserSharedPref(requireContext()).getToken())
        CoroutineScope(Dispatchers.IO).launch {
            try {
                apiService?.checkProductPresentInWishlist(addToWishListRequestModel)
                    ?.enqueue(object : Callback<RegisterResponse?> {
                        override fun onResponse(
                            call: Call<RegisterResponse?>,
                            response: Response<RegisterResponse?>
                        ) {
                            if (response.isSuccessful) {
                                if (response.body() != null) {
                                    if (response.body()!!.message == "true") {
                                        binding.heartImgProDe.setImageDrawable(
                                            resources.getDrawable(
                                                R.drawable.heart
                                            )
                                        )
                                    } else {
                                        binding.heartImgProDe.setImageDrawable(
                                            resources.getDrawable(
                                                R.drawable.outlined_heart
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        override fun onFailure(call: Call<RegisterResponse?>, t: Throwable) {
                            Log.d("CHECK_WISH_PRESENT_PRODUCT", t.message.toString())
                        }

                    })

            } catch (e: Exception) {
                Log.d("CHECK_WISH_PRESENT_PRODUCT", e.message.toString())
            }
        }
    }


    private fun checkAlreadyProductInCart() {
        val apiService = ApiClient.getApiService()
        val addToWishListRequestModel =
            AddToWishListRequestModel(productUid, UserSharedPref(requireContext()).getToken())
        CoroutineScope(Dispatchers.IO).launch {
            try {
                apiService?.checkProductInCart(addToWishListRequestModel)
                    ?.enqueue(object : Callback<RegisterResponse?> {
                        override fun onResponse(
                            call: Call<RegisterResponse?>,
                            response: Response<RegisterResponse?>
                        ) {
                            if (response.isSuccessful) {
                                if (response.body() != null) {
                                    if (response.body()!!.message == "true") {
                                        isAlreadyInCart = true
                                        binding.addToCartTextProDe.text = "Go to Cart"
                                    }
                                }
                            } else {
                                Log.d(
                                    "CHECK_ALREADY_IN_CART_RES_ERROR",
                                    response.errorBody().toString()
                                )
                            }
                        }

                        override fun onFailure(call: Call<RegisterResponse?>, t: Throwable) {
                            Log.d("CHECK_ALREADY_IN_CART", t.message.toString())
                        }

                    })

            } catch (e: Exception) {
                Log.d("CHECK_ALREADY_IN_CART", e.message.toString())
            }
        }
    }

    private fun setImageAdapter(images: List<String>) {
        val adapter = ImageSliderAdapter(images)
        binding.productImgViewPager.adapter = adapter

        createDots(images.size, 0) // Initialize dots

        // Set up a listener to update dots when the page is changed
        binding.productImgViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateDots(position) // Update dots when the page is selected
            }
        })

        binding.productImgViewPager.setPageTransformer { page, position ->
//            page.translationX = -position * page.width  // Custom slide effect
//            page.alpha = 0.5f + (1 - abs(position))  // Fade effect
            when {
                position <= -1 -> {
                    // Page is off-screen to the left
                    page.alpha = 0f
                }

                position <= 0 -> {
                    // This page is coming in (the previous page)
                    page.alpha = 1f + position // Fades in from the left
                    page.translationX = 0f // No translation, keeps it in the center
                    page.scaleY = 1f // Keep original scale
                }

                position <= 1 -> {
                    // This page is going out (the current page)
                    page.alpha = 1 - abs(position) // Fades out to the right
                    page.translationX = -position * page.width // Standard slide transition
                    page.scaleY = 1f // Keep original scale
                }

                else -> {
                    // Page is off-screen to the right
                    page.alpha = 0f
                }
            }
        }

        binding.saveBtnProDe.setOnClickListener {

        }

        binding.shareProBtnProDe.setOnClickListener {

        }
    }

    private fun createDots(size: Int, currentPosition: Int) {
        val dots = arrayOfNulls<ImageView>(size)
        binding.dotsIndicator.removeAllViews()

        for (i in dots.indices) {
            dots[i] = ImageView(this.context).apply {
                setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        if (i == currentPosition) R.drawable.active_dot else R.drawable.inactive_dot
                    )
                )
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(8, 0, 8, 0)
                binding.dotsIndicator.addView(this, params)
            }
        }
    }

    private fun updateDots(position: Int) {
        val childCount = binding.dotsIndicator.childCount
        for (i in 0 until childCount) {
            val imageView = binding.dotsIndicator.getChildAt(i) as ImageView
            imageView.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    if (i == position) R.drawable.active_dot else R.drawable.inactive_dot
                )
            )
        }
    }

    private fun getProductReviews(productId: String) {
        binding.reviewShimmerEffectProDe.startShimmer()
        binding.reviewsRecyclerviewProDe.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val dividerItemDecoration =
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        dividerItemDecoration.setDrawable(resources.getDrawable(R.drawable.grey_divider))
        binding.reviewsRecyclerviewProDe.addItemDecoration(dividerItemDecoration)

        val apiService = ApiClient.getApiService()
        CoroutineScope(Dispatchers.IO).launch {
            if (apiService != null) {
                apiService.getReviewByProductId(productId)
                    ?.enqueue(object : Callback<List<ReviewModelItem>?> {
                        override fun onResponse(
                            call: Call<List<ReviewModelItem>?>,
                            response: Response<List<ReviewModelItem>?>
                        ) {
                            if (response.isSuccessful) {
                                binding.reviewShimmerEffectProDe.stopShimmer()
                                binding.reviewShimmerEffectProDe.visibility = View.GONE
                                val list = response.body()
                                if (list!!.isEmpty()) {
                                    Log.d("NOT_FOUND_REVIEW", "EUE")
                                }
                                val adapter = ReviewAdapter(requireContext(), list)
                                binding.reviewsRecyclerviewProDe.adapter = adapter
                            } else {
                                binding.reviewShimmerEffectProDe.stopShimmer()
                                binding.reviewShimmerEffectProDe.visibility = View.GONE
                                Log.d("ERROR_REVIEWS", response.errorBody().toString())
                            }
                        }

                        override fun onFailure(call: Call<List<ReviewModelItem>?>, t: Throwable) {
                            binding.reviewShimmerEffectProDe.stopShimmer()
                            binding.reviewShimmerEffectProDe.visibility = View.GONE
                            Log.d("ERROR_REVIEWS", t.message.toString())
                        }
                    })
            }
        }
    }

    private fun getSpecificationsById(productId: String) {
        binding.specsShimmerEffectProDe.startShimmer()
        val apiService = ApiClient.getApiService()
        CoroutineScope(Dispatchers.IO).launch {
            apiService?.getProductSpecificationByUid(productId)
                ?.enqueue(object : Callback<List<ProductSpecificationsItem>> {
                    override fun onResponse(
                        call: Call<List<ProductSpecificationsItem>>,
                        response: Response<List<ProductSpecificationsItem>>
                    ) {
                        if (response.isSuccessful) {
                            try {
                                if (response.body() != null) {
                                    binding.specsShimmerEffectProDe.stopShimmer()
                                    binding.specsShimmerEffectProDe.visibility = View.GONE
                                    val productSpecs = response.body()?.get(0)
                                    if (productSpecs!!.specifications.isEmpty()) {
                                        Log.d("Specs_Error", "not found")
                                    }
                                    binding.key1ProDe.text = productSpecs.specifications[0].key
                                    binding.key2ProDe.text = productSpecs.specifications[1].key
                                    binding.key3ProDe.text = productSpecs.specifications[2].key
                                    binding.key4ProDe.text = productSpecs.specifications[3].key
                                    binding.key5ProDe.text = productSpecs.specifications[4].key
                                    binding.key6ProDe.text = productSpecs.specifications[5].key

                                    binding.value1ProDe.text = productSpecs.specifications[0].value
                                    binding.value2ProDe.text = productSpecs.specifications[1].value
                                    binding.value3ProDe.text = productSpecs.specifications[2].value
                                    binding.value4ProDe.text = productSpecs.specifications[3].value
                                    binding.value5ProDe.text = productSpecs.specifications[4].value
                                    binding.value6ProDe.text = productSpecs.specifications[5].value

                                    binding.productSpecsLayoutProDe.visibility = View.VISIBLE
                                } else {
                                    binding.specsShimmerEffectProDe.stopShimmer()
                                    binding.specsShimmerEffectProDe.visibility = View.GONE
                                    binding.productSpecsLayoutProDe.visibility = View.GONE
                                    Log.d("Specs_Error", "not found")
                                }
                            } catch (err: Exception) {
                                binding.specsShimmerEffectProDe.stopShimmer()
                                binding.specsShimmerEffectProDe.visibility = View.GONE
                                binding.productSpecsLayoutProDe.visibility = View.GONE
                                Log.d("Specs_Error_Excep", err.message.toString())
                            }
                        } else {
                            binding.specsShimmerEffectProDe.stopShimmer()
                            binding.specsShimmerEffectProDe.visibility = View.GONE
                            Log.d("Specs_Error", response.errorBody().toString())
                        }
                    }

                    override fun onFailure(
                        call: Call<List<ProductSpecificationsItem>>,
                        t: Throwable
                    ) {
                        Log.d("Specs_Error", t.message.toString())
                    }
                })
        }
    }

    private fun getBySellerID(sellerUid: String) {
        binding.smallProductShimmerProDe.visibility = View.VISIBLE
        binding.smallProductShimmerProDe.startShimmer()
        binding.relatedRecyclerviewProDe.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val apiService = ApiClient.getApiService()
        CoroutineScope(Dispatchers.IO).launch {
            apiService?.getProductsBySellerId(sellerUid)
                ?.enqueue(object : Callback<ProductModel> {
                    override fun onResponse(
                        call: Call<ProductModel>,
                        response: Response<ProductModel>
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            val productList = response.body()!!.productList
                            val adapter = ProductHoriAdapter(requireContext(), productList)
                            binding.relatedRecyclerviewProDe.adapter = adapter
                            binding.smallProductShimmerProDe.visibility = View.GONE
                            binding.smallProductShimmerProDe.stopShimmer()
                        } else {
                            println("FAIL-ERROR: ${response.errorBody()?.string()}")
                            binding.smallProductShimmerProDe.visibility = View.GONE
                            binding.smallProductShimmerProDe.stopShimmer()
                        }
                    }

                    override fun onFailure(call: Call<ProductModel>, t: Throwable) {
                        println("FAIL-ERROR: ${t.message}")
                        binding.smallProductShimmerProDe.visibility = View.GONE
                        binding.smallProductShimmerProDe.stopShimmer()
                    }
                })
        }
    }


    private fun calculatePriceDropPercentage(originalPrice: Int, sellingPrice: Int): Int {
        return (((originalPrice - sellingPrice).toDouble() / originalPrice) * 100).toInt()
    }

    private fun formatNumberWithCommas(number: Int): String {
        val formatter = NumberFormat.getInstance(Locale("en", "IN"))
        return formatter.format(number)
    }

    private fun setRatings(reviewAverage: Double) {

        if (reviewAverage > 0 && reviewAverage < 1) {
            binding.star1ProDe.setImageResource(R.drawable.half_rating)
            binding.star2ProDe.setImageResource(R.drawable.blank_star)
            binding.star3ProDe.setImageResource(R.drawable.blank_star)
            binding.star4ProDe.setImageResource(R.drawable.blank_star)
            binding.star5ProDe.setImageResource(R.drawable.blank_star)
        } else if (reviewAverage >= 1 && reviewAverage < 1.5) {
            binding.star1ProDe.setImageResource(R.drawable.full_star)
            binding.star2ProDe.setImageResource(R.drawable.blank_star)
            binding.star3ProDe.setImageResource(R.drawable.blank_star)
            binding.star4ProDe.setImageResource(R.drawable.blank_star)
            binding.star5ProDe.setImageResource(R.drawable.blank_star)
        } else if (reviewAverage >= 1.5 && reviewAverage < 2) {
            binding.star1ProDe.setImageResource(R.drawable.full_star)
            binding.star2ProDe.setImageResource(R.drawable.half_rating)
            binding.star3ProDe.setImageResource(R.drawable.blank_star)
            binding.star4ProDe.setImageResource(R.drawable.blank_star)
            binding.star5ProDe.setImageResource(R.drawable.blank_star)
        } else if (reviewAverage >= 2 && reviewAverage < 2.5) {
            binding.star1ProDe.setImageResource(R.drawable.full_star)
            binding.star2ProDe.setImageResource(R.drawable.full_star)
            binding.star3ProDe.setImageResource(R.drawable.blank_star)
            binding.star4ProDe.setImageResource(R.drawable.blank_star)
            binding.star5ProDe.setImageResource(R.drawable.blank_star)
        } else if (reviewAverage >= 2.5 && reviewAverage < 3) {
            binding.star1ProDe.setImageResource(R.drawable.full_star)
            binding.star2ProDe.setImageResource(R.drawable.full_star)
            binding.star3ProDe.setImageResource(R.drawable.half_rating)
            binding.star4ProDe.setImageResource(R.drawable.blank_star)
            binding.star5ProDe.setImageResource(R.drawable.blank_star)
        } else if (reviewAverage >= 3 && reviewAverage < 3.5) {
            binding.star1ProDe.setImageResource(R.drawable.full_star)
            binding.star2ProDe.setImageResource(R.drawable.full_star)
            binding.star3ProDe.setImageResource(R.drawable.full_star)
            binding.star4ProDe.setImageResource(R.drawable.blank_star)
            binding.star5ProDe.setImageResource(R.drawable.blank_star)
        } else if (reviewAverage >= 3.5 && reviewAverage < 4) {
            binding.star1ProDe.setImageResource(R.drawable.full_star)
            binding.star2ProDe.setImageResource(R.drawable.full_star)
            binding.star3ProDe.setImageResource(R.drawable.full_star)
            binding.star4ProDe.setImageResource(R.drawable.half_rating)
            binding.star5ProDe.setImageResource(R.drawable.blank_star)
        } else if (reviewAverage >= 4 && reviewAverage < 4.5) {
            binding.star1ProDe.setImageResource(R.drawable.full_star)
            binding.star2ProDe.setImageResource(R.drawable.full_star)
            binding.star3ProDe.setImageResource(R.drawable.full_star)
            binding.star4ProDe.setImageResource(R.drawable.full_star)
            binding.star5ProDe.setImageResource(R.drawable.blank_star)
        } else if (reviewAverage >= 4.5 && reviewAverage < 5) {
            binding.star1ProDe.setImageResource(R.drawable.full_star)
            binding.star2ProDe.setImageResource(R.drawable.full_star)
            binding.star3ProDe.setImageResource(R.drawable.full_star)
            binding.star4ProDe.setImageResource(R.drawable.full_star)
            binding.star5ProDe.setImageResource(R.drawable.half_rating)
        } else if (reviewAverage >= 5) {
            binding.star1ProDe.setImageResource(R.drawable.full_star)
            binding.star2ProDe.setImageResource(R.drawable.full_star)
            binding.star3ProDe.setImageResource(R.drawable.full_star)
            binding.star4ProDe.setImageResource(R.drawable.full_star)
            binding.star5ProDe.setImageResource(R.drawable.full_star)
        }
    }
}