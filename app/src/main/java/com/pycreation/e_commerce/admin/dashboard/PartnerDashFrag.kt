package com.pycreation.e_commerce.admin.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.pycreation.e_commerce.R
import com.pycreation.e_commerce.UserSharedPref
import com.pycreation.e_commerce.admin.listening.adapter.ProductAdapterPAcc
import com.pycreation.e_commerce.admin.models.productModel.ProductModel
import com.pycreation.e_commerce.databinding.FragmentPartnerDashBinding
import com.pycreation.e_commerce.retrofit.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class PartnerDashFrag : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentPartnerDashBinding
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
        binding = FragmentPartnerDashBinding.inflate(inflater, container, false)
        binding.sellerProductsRecyclerview.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        getBySellerID()
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PartnerDashFrag().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun getBySellerID() {
        binding.sellerProductShimmerPartnerDash.visibility = View.VISIBLE
        binding.sellerProductShimmerPartnerDash.startShimmer()
        val apiService = ApiClient.getApiService()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                apiService?.getProductsBySellerId(UserSharedPref(requireContext()).getToken())
                    ?.enqueue(object : Callback<ProductModel> {
                        override fun onResponse(
                            call: Call<ProductModel>,
                            response: Response<ProductModel>
                        ) {
                            if (response.isSuccessful && response.body() != null) {
                                val productList = response.body()!!.productList
//                                println("GET_BY_CAT: ${response.body()!!.productList[0]}")
                                val adapter = ProductAdapterPAcc(requireContext(), productList)
                                binding.sellerProductsRecyclerview.adapter = adapter
                                binding.sellerProductShimmerPartnerDash.visibility = View.GONE
                                binding.sellerProductShimmerPartnerDash.stopShimmer()
                            } else {
                                println("FAIL-ERROR: ${response.errorBody()?.string()}")
                                binding.sellerProductShimmerPartnerDash.visibility = View.GONE
                                binding.sellerProductShimmerPartnerDash.stopShimmer()
                            }
                        }

                        override fun onFailure(call: Call<ProductModel>, t: Throwable) {
                            println("FAIL-ERROR: ${t.message}")
                            binding.sellerProductShimmerPartnerDash.visibility = View.GONE
                            binding.sellerProductShimmerPartnerDash.stopShimmer()
                        }
                    })
            } catch (err: Exception) {
                println("FAIL-ERROR: ${err.message.toString()}")
                binding.sellerProductShimmerPartnerDash.visibility = View.GONE
                binding.sellerProductShimmerPartnerDash.stopShimmer()
            }
        }
    }
}