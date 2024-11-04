package com.pycreation.e_commerce.common.reviews.activities

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
import com.pycreation.e_commerce.common.reviews.adaptors.ReviewListAdapter
import com.pycreation.e_commerce.common.reviews.models.ReviewWIthDetailsResModel
import com.pycreation.e_commerce.consumer.dashboard.ConsumerDashboard
import com.pycreation.e_commerce.databinding.FragmentReviewListBinding
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

class ReviewListFrag : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentReviewListBinding

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
        binding = FragmentReviewListBinding.inflate(inflater, container, false)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().supportFragmentManager.popBackStack()
        }
        binding.reviewListRecyclerview.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        checkInternetConnection()
        setupActionBar()
        getAllWrittenReview()
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ReviewListFrag().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun checkInternetConnection() {
        if ((activity as ConsumerDashboard?)?.isNetworkAvailable() == true) {
            binding.internetLyReviewList.visibility = View.GONE
            binding.reviewListLayout.visibility = View.VISIBLE
        } else {
            binding.internetLyReviewList.visibility = View.VISIBLE
            binding.reviewListLayout.visibility = View.GONE
        }
    }


    private fun getAllWrittenReview() {
        val apiClient = ApiClient.getApiService()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                apiClient?.getAllConsumerReviews(UserSharedPref(requireContext()).getToken())
                    ?.enqueue(object : Callback<ReviewWIthDetailsResModel?> {
                        override fun onResponse(
                            call: Call<ReviewWIthDetailsResModel?>,
                            response: Response<ReviewWIthDetailsResModel?>
                        ) {
                            if (response.isSuccessful) {
                                val adapter =
                                    ReviewListAdapter(requireContext(), response.body()!!.reviews)
                                binding.reviewListRecyclerview.adapter = adapter
                                Log.d("GET_REVIEW_BY_CONSUMER", response.body().toString())
                            } else {
                                Log.d("GET_REVIEW_BY_CONSUMER", response.errorBody().toString())
                            }
                        }

                        override fun onFailure(
                            call: Call<ReviewWIthDetailsResModel?>,
                            t: Throwable
                        ) {
                            Log.d("GET_REVIEW_BY_CONSUMER", t.message.toString())
                        }

                    })
            } catch (e: Exception) {
                Log.d("GET_REVIEW_BY_CONSUMER", e.message.toString())
            }
        }
    }

    private fun setupActionBar() {
        (activity as AppCompatActivity).setSupportActionBar(binding.reviewListToolbar)
        val actionBar = (activity as AppCompatActivity).supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
        }
        binding.reviewListToolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }
}