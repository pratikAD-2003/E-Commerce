package com.pycreation.e_commerce.consumer.orders.activities

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.pycreation.e_commerce.R
import com.pycreation.e_commerce.consumer.dashboard.ConsumerDashboard
import com.pycreation.e_commerce.consumer.orders.adaptors.OrderedProductsAdapter
import com.pycreation.e_commerce.consumer.orders.res.Order
import com.pycreation.e_commerce.databinding.FragmentOrderItemDetailsBinding
import java.text.NumberFormat
import java.util.Locale

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class OrderItemDetails : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentOrderItemDetailsBinding
    private lateinit var currentOrder: Order

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
        binding = FragmentOrderItemDetailsBinding.inflate(inflater, container, false)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().supportFragmentManager.popBackStack()
        }
        setupActionBar()
        checkInternetConnection()
        val gson = Gson()
        currentOrder = gson.fromJson(arguments?.getString("data"), Order::class.java)
        binding.deliveredAddressOrderedDetails.text = currentOrder.deliveryAddress
        binding.subTotalOrderDetails.text =
            "₹" + formatNumberWithCommas(currentOrder.orderTotalPrice).toString()
        if (currentOrder.orderShippingCost == 0) {
            binding.deliveryChargesOrderDetails.text = "Free Delivery"
        } else {
            binding.deliveryChargesOrderDetails.text =
                "₹" + formatNumberWithCommas(currentOrder.orderShippingCost).toString()
        }
        binding.platformFeeOrdersDetails.text =
            "₹" + formatNumberWithCommas((currentOrder.totalPrice - (currentOrder.orderTotalPrice + currentOrder.orderShippingCost)).toInt()).toString() + "(2%)"
        binding.totalOrderDetails.text =
            "₹" + formatNumberWithCommas(currentOrder.totalPrice.toInt()).toString()
        if (currentOrder.paymentStatus == "Completed") {
            binding.paymentTotalTextOrderItemDetails.text = "Paid Amount"
        } else {
            binding.paymentTotalTextOrderItemDetails.text = "Payable Amount"
        }
        binding.orderedProductsRecyclerviewOrderItemDetails.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val adapter =
            OrderedProductsAdapter(requireContext(), currentOrder.items, currentOrder.orderStatus)
        binding.orderedProductsRecyclerviewOrderItemDetails.adapter = adapter

        binding.retryOrderDetailsBtn.setOnClickListener {

        }
        setUpOrderStatus()
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OrderItemDetails().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun checkInternetConnection() {
        if ((activity as ConsumerDashboard?)?.isNetworkAvailable() == true) {
            binding.internetLyOrderDetailsPage.visibility = View.GONE
            binding.orderDetailsLayout.visibility = View.VISIBLE
        } else {
            binding.internetLyOrderDetailsPage.visibility = View.VISIBLE
            binding.orderDetailsLayout.visibility = View.GONE
        }
    }

    private fun setUpOrderStatus() {
        binding.ordersStatusItem.text = currentOrder.orderStatus
        binding.paymentStatusOrderDetails.text = currentOrder.paymentStatus

        when (currentOrder.orderStatus) {
            "Placed" -> {

            }

            "Shipped" -> {
                binding.shippedOrderLineItem.setBackgroundColor(resources.getColor(R.color.orange))
                binding.shippedCircleItem.setImageResource(R.drawable.oval_red)
                binding.shippedTextItem.setTextColor(resources.getColor(R.color.orange))
                binding.shippedIconItem.imageTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.orange))
            }

            "Out of Delivery" -> {
                binding.shippedOrderLineItem.setBackgroundColor(resources.getColor(R.color.orange))
                binding.shippedCircleItem.setImageResource(R.drawable.oval_red)
                binding.shippedTextItem.setTextColor(resources.getColor(R.color.orange))
                binding.shippedIconItem.imageTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.orange))

                binding.outOfOrderLineItem.setBackgroundColor(resources.getColor(R.color.orange))
                binding.outOfDeliveryCircleItem.setImageResource(R.drawable.oval_red)
                binding.outOfDeliveryTextItem.setTextColor(resources.getColor(R.color.orange))
                binding.outOfDeliveryIconItem.imageTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.orange))
            }

            "Delivered" -> {
                binding.shippedOrderLineItem.setBackgroundColor(resources.getColor(R.color.orange))
                binding.shippedCircleItem.setImageResource(R.drawable.oval_red)
                binding.shippedTextItem.setTextColor(resources.getColor(R.color.orange))
                binding.shippedIconItem.imageTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.orange))

                binding.outOfOrderLineItem.setBackgroundColor(resources.getColor(R.color.orange))
                binding.outOfDeliveryCircleItem.setImageResource(R.drawable.oval_red)
                binding.outOfDeliveryTextItem.setTextColor(resources.getColor(R.color.orange))
                binding.outOfDeliveryIconItem.imageTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.orange))

                binding.deliveredLineItem.setBackgroundColor(resources.getColor(R.color.orange))
                binding.deliveredCircleItem.setImageResource(R.drawable.oval_red)
                binding.deliveryTextItem.setTextColor(resources.getColor(R.color.orange))
                binding.deliveredIconItem.imageTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.orange))
            }

            "Canceled" -> {

            }
        }
    }

    private fun formatNumberWithCommas(number: Int): String {
        val formatter = NumberFormat.getInstance(Locale("en", "IN"))
        return formatter.format(number)
    }

    private fun setupActionBar() {
        (activity as AppCompatActivity).setSupportActionBar(binding.orderDetailsToolbar)
        val actionBar = (activity as AppCompatActivity).supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
        }
        binding.orderDetailsToolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }
}