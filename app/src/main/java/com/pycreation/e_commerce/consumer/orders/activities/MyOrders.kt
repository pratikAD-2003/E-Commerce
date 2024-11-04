package com.pycreation.e_commerce.consumer.orders.activities

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.pycreation.e_commerce.APIs
import com.pycreation.e_commerce.R
import com.pycreation.e_commerce.UserSharedPref
import com.pycreation.e_commerce.consumer.dashboard.ConsumerDashboard
import com.pycreation.e_commerce.consumer.orders.adaptors.MyOrdersAdapter
import com.pycreation.e_commerce.consumer.orders.res.OrderListModelRes
import com.pycreation.e_commerce.databinding.FragmentMyOrdersBinding
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

class MyOrders : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentMyOrdersBinding
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
        binding = FragmentMyOrdersBinding.inflate(inflater, container, false)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().supportFragmentManager.popBackStack()
        }
        checkInternetConnection()

        binding.orderRecyclerviewMyOrders.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        setupActionBar()
        getMyOrders()

        binding.retryMyOrdersBtn.setOnClickListener {

        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MyOrders().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun checkInternetConnection(){
        if ((activity as ConsumerDashboard?)?.isNetworkAvailable() == true) {
            binding.internetLyMyOrders.visibility = View.GONE
            binding.myOrdersLayout.visibility = View.VISIBLE
        } else {
            binding.internetLyMyOrders.visibility = View.VISIBLE
            binding.myOrdersLayout.visibility = View.GONE
        }
    }

    private fun getMyOrders() {
        binding.myOrderPageShimmer.visibility = View.VISIBLE
        binding.myOrderPageShimmer.startShimmer()
        val apiClient = ApiClient.getApiService()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                apiClient?.getOrderedProducts(UserSharedPref(requireContext()).getToken())
                    ?.enqueue(object : Callback<OrderListModelRes?> {
                        override fun onResponse(
                            call: Call<OrderListModelRes?>,
                            response: Response<OrderListModelRes?>
                        ) {
                            if (response.isSuccessful) {
                                binding.myOrderPageShimmer.stopShimmer()
                                binding.myOrderPageShimmer.visibility = View.GONE
                                val adapter =
                                    MyOrdersAdapter(requireContext(), response.body()!!.orders)
                                binding.orderRecyclerviewMyOrders.adapter = adapter
                                Log.d("MY_ORDERS", response.body().toString())
                            } else {
                                binding.myOrderPageShimmer.stopShimmer()
                                binding.myOrderPageShimmer.visibility = View.GONE
                                Log.d("MY_ORDERS", response.errorBody().toString())
                            }
                        }

                        override fun onFailure(call: Call<OrderListModelRes?>, t: Throwable) {
                            binding.myOrderPageShimmer.stopShimmer()
                            binding.myOrderPageShimmer.visibility = View.GONE
                            Log.d("MY_ORDERS", t.message.toString())
                        }

                    })
            } catch (e: Exception) {
                binding.myOrderPageShimmer.stopShimmer()
                binding.myOrderPageShimmer.visibility = View.GONE
                Log.d("MY_ORDERS", e.message.toString())
            }
        }
    }


    private fun setupActionBar() {
        (activity as AppCompatActivity).setSupportActionBar(binding.myOrdersToolbar)
        val actionBar = (activity as AppCompatActivity).supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
        }
        binding.myOrdersToolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }
}