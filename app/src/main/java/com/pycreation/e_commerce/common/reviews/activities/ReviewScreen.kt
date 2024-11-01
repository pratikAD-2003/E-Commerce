package com.pycreation.e_commerce.common.reviews.activities

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import com.pycreation.e_commerce.R
import com.pycreation.e_commerce.UserSharedPref
import com.pycreation.e_commerce.common.reviews.models.GetReviewByConReqModel
import com.pycreation.e_commerce.common.reviews.models.PostReviewReqModel
import com.pycreation.e_commerce.common.reviews.models.ReviewModelItem
import com.pycreation.e_commerce.consumer.dashboard.ConsumerDashboard
import com.pycreation.e_commerce.consumer.responseModels.RegisterResponse
import com.pycreation.e_commerce.databinding.FragmentReviewScreenBinding
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

class ReviewScreen : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentReviewScreenBinding
    private var selectedRating = "null"

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
        binding = FragmentReviewScreenBinding.inflate(inflater, container, false)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().supportFragmentManager.popBackStack()
        }
        val productUid = arguments?.getString("productUid")
        val productName = arguments?.getString("name")
        checkAlreadyPostedReview(productUid!!)

        binding.productNameReviewScreen.text = productName
        setupActionBar()
        setupRatingSelection()

        binding.leaveReviewBtbReviewScreen.setOnClickListener {
            if (checkValidation()) {
                postProductReview(productUid)
            }
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ReviewScreen().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun checkAlreadyPostedReview(productUid: String) {
        val apiClient = ApiClient.getApiService()
        val getReviewByConReqModel =
            GetReviewByConReqModel(productUid, UserSharedPref(requireContext()).getToken())
        CoroutineScope(Dispatchers.IO).launch {
            try {
                apiClient?.getReviewByConsumerId(getReviewByConReqModel)
                    ?.enqueue(object : Callback<List<ReviewModelItem?>?> {
                        override fun onResponse(
                            call: Call<List<ReviewModelItem?>?>,
                            response: Response<List<ReviewModelItem?>?>
                        ) {
                            if (response.isSuccessful) {
                                Log.d("GET_REVIEW_BY_CONSUMER", response.body().toString())
                                if (response.body() != null) {
                                    if (response.body()!!.isNotEmpty()) {
                                        binding.leaveReviewBtbReviewScreen.visibility = View.GONE
                                        binding.reviewerNameReviewScreen.setText(response.body()!![0]!!.reviewerName)
                                        binding.reviewTextReviewScreen.setText(response.body()!![0]!!.reviewText)
                                        setupAlreadyRating(response.body()!![0]!!.rating)
                                    }
                                }
                            } else {
                                Log.d("GET_REVIEW_BY_CONSUMER", response.errorBody().toString())
                            }
                        }

                        override fun onFailure(call: Call<List<ReviewModelItem?>?>, t: Throwable) {
                            Log.d("GET_REVIEW_BY_CONSUMER", t.message.toString())
                        }

                    })
            } catch (e: Exception) {
                Log.d("GET_REVIEW_BY_CONSUMER", e.message.toString())
            }
        }
    }

    private fun postProductReview(productUid: String) {
        (activity as ConsumerDashboard?)?.showDialog("Wait...")
        val reviewerName = binding.reviewerNameReviewScreen.text.toString()
        val reviewText = binding.reviewTextReviewScreen.text.toString()
        val postReviewReqModel = PostReviewReqModel(
            productUid,
            selectedRating,
            reviewText,
            UserSharedPref(requireContext()).getToken(),
            reviewerName
        )
        val apiClient = ApiClient.getApiService()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                apiClient?.postReviewByConsumer(postReviewReqModel)
                    ?.enqueue(object : Callback<RegisterResponse?> {
                        override fun onResponse(
                            call: Call<RegisterResponse?>,
                            response: Response<RegisterResponse?>
                        ) {
                            if (response.isSuccessful) {
                                (activity as ConsumerDashboard?)?.dismissDialog()
                                (activity as ConsumerDashboard?)?.showError(response.body()!!.message)
                                Log.d("POST_REVIEW_ERROR", response.body().toString())
                                requireActivity().supportFragmentManager.popBackStack()
                            } else {
                                (activity as ConsumerDashboard?)?.dismissDialog()
                                Log.d("POST_REVIEW_ERROR", response.errorBody().toString())
                            }
                        }

                        override fun onFailure(call: Call<RegisterResponse?>, t: Throwable) {
                            (activity as ConsumerDashboard?)?.dismissDialog()
                            Log.d("POST_REVIEW_ERROR", t.message.toString())
                        }

                    })
            } catch (e: Exception) {
                (activity as ConsumerDashboard?)?.dismissDialog()
                Log.d("POST_REVIEW_ERROR", e.message.toString())
            }
        }
    }

    private fun checkValidation(): Boolean {
        val reviewerName = binding.reviewerNameReviewScreen.text.toString()
        val reviewText = binding.reviewTextReviewScreen.text.toString()
        if (reviewerName.isEmpty() || reviewText.isEmpty()) {
            (activity as ConsumerDashboard?)?.showError("Please fill all details!")
            return false
        }
        if (selectedRating == "null") {
            (activity as ConsumerDashboard?)?.showError("Please select Rating!")
            return false
        }
        return true
    }

    private fun setupRatingSelection() {
        binding.star1ReviewScreen.setOnClickListener {
            selectedRating = "1"
            binding.star1ReviewScreen.setImageResource(R.drawable.full_star)
            binding.star2ReviewScreen.setImageResource(R.drawable.blank_star)
            binding.star3ReviewScreen.setImageResource(R.drawable.blank_star)
            binding.star4ReviewScreen.setImageResource(R.drawable.blank_star)
            binding.star5ReviewScreen.setImageResource(R.drawable.blank_star)
        }

        binding.star2ReviewScreen.setOnClickListener {
            selectedRating = "2"
            binding.star1ReviewScreen.setImageResource(R.drawable.full_star)
            binding.star2ReviewScreen.setImageResource(R.drawable.full_star)
            binding.star3ReviewScreen.setImageResource(R.drawable.blank_star)
            binding.star4ReviewScreen.setImageResource(R.drawable.blank_star)
            binding.star5ReviewScreen.setImageResource(R.drawable.blank_star)
        }

        binding.star3ReviewScreen.setOnClickListener {
            selectedRating = "3"
            binding.star1ReviewScreen.setImageResource(R.drawable.full_star)
            binding.star2ReviewScreen.setImageResource(R.drawable.full_star)
            binding.star3ReviewScreen.setImageResource(R.drawable.full_star)
            binding.star4ReviewScreen.setImageResource(R.drawable.blank_star)
            binding.star5ReviewScreen.setImageResource(R.drawable.blank_star)
        }

        binding.star4ReviewScreen.setOnClickListener {
            selectedRating = "4"
            binding.star1ReviewScreen.setImageResource(R.drawable.full_star)
            binding.star2ReviewScreen.setImageResource(R.drawable.full_star)
            binding.star3ReviewScreen.setImageResource(R.drawable.full_star)
            binding.star4ReviewScreen.setImageResource(R.drawable.full_star)
            binding.star5ReviewScreen.setImageResource(R.drawable.blank_star)
        }

        binding.star5ReviewScreen.setOnClickListener {
            selectedRating = "5"
            binding.star1ReviewScreen.setImageResource(R.drawable.full_star)
            binding.star2ReviewScreen.setImageResource(R.drawable.full_star)
            binding.star3ReviewScreen.setImageResource(R.drawable.full_star)
            binding.star4ReviewScreen.setImageResource(R.drawable.full_star)
            binding.star5ReviewScreen.setImageResource(R.drawable.full_star)
        }
    }

    private fun setupAlreadyRating(rating: Int) {
        when (rating) {
            1 -> {
                binding.star1ReviewScreen.setImageResource(R.drawable.full_star)
                binding.star2ReviewScreen.setImageResource(R.drawable.blank_star)
                binding.star3ReviewScreen.setImageResource(R.drawable.blank_star)
                binding.star4ReviewScreen.setImageResource(R.drawable.blank_star)
                binding.star5ReviewScreen.setImageResource(R.drawable.blank_star)
            }

            2 -> {
                binding.star1ReviewScreen.setImageResource(R.drawable.full_star)
                binding.star2ReviewScreen.setImageResource(R.drawable.full_star)
                binding.star3ReviewScreen.setImageResource(R.drawable.blank_star)
                binding.star4ReviewScreen.setImageResource(R.drawable.blank_star)
                binding.star5ReviewScreen.setImageResource(R.drawable.blank_star)
            }

            3 -> {
                binding.star1ReviewScreen.setImageResource(R.drawable.full_star)
                binding.star2ReviewScreen.setImageResource(R.drawable.full_star)
                binding.star3ReviewScreen.setImageResource(R.drawable.full_star)
                binding.star4ReviewScreen.setImageResource(R.drawable.blank_star)
                binding.star5ReviewScreen.setImageResource(R.drawable.blank_star)
            }

            4 -> {
                binding.star1ReviewScreen.setImageResource(R.drawable.full_star)
                binding.star2ReviewScreen.setImageResource(R.drawable.full_star)
                binding.star3ReviewScreen.setImageResource(R.drawable.full_star)
                binding.star4ReviewScreen.setImageResource(R.drawable.full_star)
                binding.star5ReviewScreen.setImageResource(R.drawable.blank_star)
            }

            5 -> {
                binding.star1ReviewScreen.setImageResource(R.drawable.full_star)
                binding.star2ReviewScreen.setImageResource(R.drawable.full_star)
                binding.star3ReviewScreen.setImageResource(R.drawable.full_star)
                binding.star4ReviewScreen.setImageResource(R.drawable.full_star)
                binding.star5ReviewScreen.setImageResource(R.drawable.full_star)
            }
        }
    }

    private fun setupActionBar() {
        (activity as AppCompatActivity).setSupportActionBar(binding.reviewScreenToolbar)
        val actionBar = (activity as AppCompatActivity).supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
        }
        binding.reviewScreenToolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }
}