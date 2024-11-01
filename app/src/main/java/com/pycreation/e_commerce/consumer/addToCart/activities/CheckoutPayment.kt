package com.pycreation.e_commerce.consumer.addToCart.activities

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.google.gson.Gson
import com.pycreation.e_commerce.R
import com.pycreation.e_commerce.UserSharedPref
import com.pycreation.e_commerce.consumer.addToCart.request.PlacedOrderReqModel
import com.pycreation.e_commerce.consumer.dashboard.ConsumerDashboard
import com.pycreation.e_commerce.consumer.orders.activities.OrderItemDetails
import com.pycreation.e_commerce.consumer.orders.req.OrderByOrderIdReqModel
import com.pycreation.e_commerce.consumer.orders.res.OrderListModelRes
import com.pycreation.e_commerce.consumer.responseModels.RegisterResponse
import com.pycreation.e_commerce.databinding.FragmentCheckoutPaymentBinding
import com.pycreation.e_commerce.retrofit.ApiClient
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultListener
import com.razorpay.PaymentResultWithDataListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class CheckoutPayment : Fragment(), PaymentResultListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentCheckoutPaymentBinding
    private lateinit var razorpay: Checkout
    private var isPaymentStatusSelected = "null"
    private lateinit var address: String

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
        binding = FragmentCheckoutPaymentBinding.inflate(inflater, container, false)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().supportFragmentManager.popBackStack()
        }
        setupActionBar()

        val amount = arguments?.getString("amount")
        val cod = arguments?.getString("cod")
        address = arguments?.getString("address").toString()

        binding.payableAmountTextCheckoutPay.text =
            "Payable Amount ₹" + formatNumberWithCommas(amount!!.toInt())

        if (cod == "true") {
            binding.cashOnDeliveryCheckoutPayment.visibility = View.VISIBLE
        } else {
            binding.cashOnDeliveryCheckoutPayment.visibility = View.GONE
        }

        razorpay = Checkout()
        razorpay.setKeyID("rzp_test_Nqy8gmPWtyPySL")

        binding.cashOnDeliveryCheckoutPayment.setOnClickListener {
            isPaymentStatusSelected = "cod"
            buttonSelected(binding.cashOnDeliveryImgCheckoutPay)
            buttonUnSelected(binding.onlinePayImgCheckoutPay)
        }

        binding.onlinePayCheckoutPayment.setOnClickListener {
            isPaymentStatusSelected = "online"
            buttonSelected(binding.onlinePayImgCheckoutPay)
            buttonUnSelected(binding.cashOnDeliveryImgCheckoutPay)
        }

        binding.testRazorPay.setOnClickListener {
            if (isPaymentStatusSelected == "null") {
                (activity as ConsumerDashboard?)?.showError("Please select an Option!")
            } else if (isPaymentStatusSelected == "cod") {
                goForCheckout(address, "Cash on Delivery")
            } else {
                startPayment(amount)
            }
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CheckoutPayment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onResume() {
        super.onResume()
        Log.d(
            "ONLINE_PAYMENT_STATUS",
            (activity as ConsumerDashboard?)?.checkPaymentStatus().toString()
        )
        if ((activity as ConsumerDashboard?)?.checkPaymentStatus() == true) {
            goForCheckout(address, "Online")
        }
    }

    private fun goForCheckout(deliveryAddress: String, paymentMethod: String) {
        (activity as ConsumerDashboard?)?.showDialog("Wait...")
        val apiService = ApiClient.getApiService()
        CoroutineScope(Dispatchers.IO).launch {
            val placedOrderReqModel = PlacedOrderReqModel(
                deliveryAddress,
                paymentMethod,
                UserSharedPref(requireContext()).getToken()
            )
            try {
                apiService?.placedOrderProducts(placedOrderReqModel)
                    ?.enqueue(object : Callback<RegisterResponse?> {
                        override fun onResponse(
                            call: Call<RegisterResponse?>,
                            response: Response<RegisterResponse?>
                        ) {
                            if (response.isSuccessful) {
                                (activity as ConsumerDashboard?)?.dismissDialog()
                                (activity as ConsumerDashboard?)?.showError("Order Placed Successfully!")
                                (activity as ConsumerDashboard?)?.resetPaymentStatus()
                                val orderId = response.body()!!.message
                                Log.d("CHECKOUT_ERROR", response.body()!!.message)
                                getOrderAfterPlacedOrder(orderId)
//                                (activity as ConsumerDashboard?)?.showError(response.body()!!.message) // get orderId and find
                            } else {
                                Log.d("CHECKOUT_ERROR", response.errorBody().toString())
                                (activity as ConsumerDashboard?)?.resetPaymentStatus()
                                (activity as ConsumerDashboard?)?.dismissDialog()
                            }
                        }

                        override fun onFailure(call: Call<RegisterResponse?>, t: Throwable) {
                            (activity as ConsumerDashboard?)?.dismissDialog()
                            (activity as ConsumerDashboard?)?.resetPaymentStatus()
                            Log.d("CHECKOUT_ERROR", t.message.toString())
                        }

                    })

            } catch (e: Exception) {
                Log.d("CHECKOUT_ERROR", e.message.toString())
                (activity as ConsumerDashboard?)?.resetPaymentStatus()
                (activity as ConsumerDashboard?)?.dismissDialog()
            }
        }
    }

    private fun getOrderAfterPlacedOrder(orderId: String) {
        (activity as ConsumerDashboard?)?.showDialog("Wait...")
        val apiService = ApiClient.getApiService()
        val orderByOrderIdReqModel =
            OrderByOrderIdReqModel(orderId, UserSharedPref(requireContext()).getToken())
        CoroutineScope(Dispatchers.IO).launch {
            try {
                apiService?.getOrderByOrderId(orderByOrderIdReqModel)
                    ?.enqueue(object : Callback<OrderListModelRes?> {
                        override fun onResponse(
                            call: Call<OrderListModelRes?>,
                            response: Response<OrderListModelRes?>
                        ) {
                            if (response.isSuccessful) {
                                (activity as ConsumerDashboard?)?.dismissDialog()
                                Log.d("GET_ORDER_BY_ORDER_ID", response.body().toString())
                                if (response.body() != null) {
                                    if (response.body()!!.orders.isNotEmpty()) {
                                        val order = response.body()!!.orders[0]
                                        val gson = Gson()
                                        val data = gson.toJson(order)
                                        val bundle = Bundle()
                                        val orderItemDetailsFrag = OrderItemDetails()
                                        bundle.putString("data", data)
                                        orderItemDetailsFrag.arguments = bundle
                                        requireActivity().supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                                        (context as ConsumerDashboard?)?.navigateTo(
                                            orderItemDetailsFrag
                                        )
                                    }
                                }
                            } else {
                                (activity as ConsumerDashboard?)?.dismissDialog()
                                Log.d("GET_ORDER_BY_ORDER_ID", response.errorBody().toString())
                            }
                        }

                        override fun onFailure(call: Call<OrderListModelRes?>, t: Throwable) {
                            (activity as ConsumerDashboard?)?.dismissDialog()
                            Log.d("GET_ORDER_BY_ORDER_ID", t.message.toString())
                        }

                    })
            } catch (e: Exception) {
                (activity as ConsumerDashboard?)?.dismissDialog()
                Log.d("GET_ORDER_BY_ORDER_ID", e.message.toString())
            }
        }
    }

    private fun setupActionBar() {
        (activity as AppCompatActivity).setSupportActionBar(binding.checkoutPaymentToolbar)
        val actionBar = (activity as AppCompatActivity).supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
        }
        binding.checkoutPaymentToolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun buttonSelected(imageView: ImageView) {
        imageView.setImageResource(R.drawable.oval_red)
        imageView.setBackgroundDrawable(resources.getDrawable(R.drawable.black_outlined))
    }

    private fun buttonUnSelected(imageView: ImageView) {
        imageView.setImageResource(R.drawable.white_oval)
        imageView.setBackgroundDrawable(resources.getDrawable(R.drawable.grey_outlined))
    }

    private fun startPayment(amount: String) {
        val amount = amount.toLong() * 100
        val co = Checkout()
        co.setKeyID("rzp_test_Nqy8gmPWtyPySL")
        try {
            val options = JSONObject()
            options.put("name", "E-Commerce App")
            options.put("description", "Product Purchase")
            options.put("image", "http://example.com/image/rzp.jpg")
            options.put("theme.color", resources.getColor(R.color.orange));
            options.put("currency", "INR");
//            options.put("order_id", "order_DBJOWzybf0sJbb");
            options.put("amount", amount)//pass amount in currency subunits

            val retryObj = JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            options.put("retry", retryObj);

            val prefill = JSONObject()
            prefill.put("email", "test@example.com")
            prefill.put("contact", "8899556688")

            options.put("prefill", prefill)
            co.open(requireActivity(), options)
        } catch (e: Exception) {
            Log.d("PAYMENT_ERROR_CATCH", e.message.toString())
            Toast.makeText(activity, "Error in payment: " + e.message, Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    override fun onPaymentSuccess(p0: String?) {
        Log.d("PAYMENT_ERROR", "Success ")
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        Log.d("PAYMENT_ERROR", p1.toString())
    }

    private fun formatNumberWithCommas(number: Int): String {
        val formatter = NumberFormat.getInstance(Locale("en", "IN"))
        return formatter.format(number)
    }

}