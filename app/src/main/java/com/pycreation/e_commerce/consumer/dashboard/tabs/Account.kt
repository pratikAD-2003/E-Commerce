package com.pycreation.e_commerce.consumer.dashboard.tabs

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.pycreation.e_commerce.R
import com.pycreation.e_commerce.UserSharedPref
import com.pycreation.e_commerce.common.ForgetPassword
import com.pycreation.e_commerce.common.reviews.activities.ReviewListFrag
import com.pycreation.e_commerce.consumer.address.activities.AddressList
import com.pycreation.e_commerce.consumer.dashboard.ConsumerDashboard
import com.pycreation.e_commerce.consumer.models.User
import com.pycreation.e_commerce.consumer.orders.activities.MyOrders
import com.pycreation.e_commerce.consumer.wishlist.activities.WishListProductsFrag
import com.pycreation.e_commerce.databinding.FragmentAccountBinding
import com.pycreation.e_commerce.onboarding.OnBoardingActivity

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Account : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentAccountBinding

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
        binding = FragmentAccountBinding.inflate(inflater, container, false)
        binding.wishlistBtnAccount.setOnClickListener {
            (activity as ConsumerDashboard?)?.navigateTo(WishListProductsFrag())
        }

        binding.savedAddressesBtnAccount.setOnClickListener {
            (activity as ConsumerDashboard?)?.navigateTo(AddressList())
        }

        binding.myOrderAccount.setOnClickListener {
            (activity as ConsumerDashboard?)?.navigateTo(MyOrders())
        }

        binding.writtenReviewLyAccount.setOnClickListener {
            (activity as ConsumerDashboard?)?.navigateTo(ReviewListFrag())
        }

        binding.changePasswordLyAccount.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("type", "from_user_dash")
            val forgetPassword = ForgetPassword()
            forgetPassword.arguments = bundle
            (activity as ConsumerDashboard?)?.navigateTo(forgetPassword)
        }

        binding.logoutBtnAccount.setOnClickListener {
            val alertDialog = AlertDialog.Builder(requireContext())
            alertDialog.setTitle("Logout!")
            alertDialog.setMessage("Are you sure, you wanna logout?")

            alertDialog.setPositiveButton("Logout") { alertDialog, _ ->
                UserSharedPref(requireContext()).deleteExistence()
                (activity as ConsumerDashboard?)?.startActivity(
                    Intent(
                        requireContext(),
                        OnBoardingActivity::class.java
                    )
                )
                (activity as ConsumerDashboard?)?.finish()
            }

            alertDialog.setNegativeButton("Cancel") { alertDialog, _ ->
                alertDialog.dismiss()
            }
            val dialog = alertDialog.create()
            dialog.show()
        }

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Account().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}