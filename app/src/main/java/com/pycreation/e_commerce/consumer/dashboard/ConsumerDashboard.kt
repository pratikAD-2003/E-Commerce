package com.pycreation.e_commerce.consumer.dashboard

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.snackbar.Snackbar
import com.pycreation.e_commerce.MyWidgets
import com.pycreation.e_commerce.R
import com.pycreation.e_commerce.admin.dashboard.PartnerDashFrag
import com.pycreation.e_commerce.admin.dashboard.PartnerStaticsFrag
import com.pycreation.e_commerce.common.productdetails.ProductDetailed
import com.pycreation.e_commerce.consumer.addToCart.activities.CheckoutPayment
import com.pycreation.e_commerce.consumer.dashboard.sub_category.activities.SubCatList
import com.pycreation.e_commerce.consumer.dashboard.subcategories.SubCategory
import com.pycreation.e_commerce.consumer.dashboard.tabs.Account
import com.pycreation.e_commerce.consumer.dashboard.tabs.Cart
import com.pycreation.e_commerce.consumer.dashboard.tabs.Categories
import com.pycreation.e_commerce.consumer.dashboard.tabs.Explore
import com.pycreation.e_commerce.consumer.dashboard.tabs.Home
import com.pycreation.e_commerce.databinding.ActivityConsumerDashboardBinding
import com.razorpay.PaymentResultListener
import org.imaginativeworld.oopsnointernet.callbacks.ConnectionCallback
import org.imaginativeworld.oopsnointernet.snackbars.fire.NoInternetSnackbarFire

class ConsumerDashboard : MyWidgets(), PaymentResultListener {
    private lateinit var binding: ActivityConsumerDashboardBinding

    private var isPaymentSuccess: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityConsumerDashboardBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.consumerDashFrameLayout, Home())
                .commit()
        }
        binding.bottomNavViewPartnerDash.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    navigateToWithoutStackTrace(Home())
                    true
                }

                R.id.category -> {
                    navigateToWithoutStackTrace(Categories())
                    true
                }

                R.id.explore -> {
                    navigateToWithoutStackTrace(Explore())
                    true
                }

                R.id.cart -> {
                    navigateToWithoutStackTrace(Cart())
                    true
                }

                R.id.profile -> {
                    navigateToWithoutStackTrace(Account())
                    true
                }

                else -> false
            }
        }
    }

    fun setHomeIcon() {
        binding.bottomNavViewPartnerDash.selectedItemId = R.id.home
    }

    fun setCartIcon() {
        binding.bottomNavViewPartnerDash.selectedItemId = R.id.cart
    }

    fun setAccountIcon() {
        binding.bottomNavViewPartnerDash.selectedItemId = R.id.profile
    }

    override fun navigateTo(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.consumerDashFrameLayout, fragment)
            .addToBackStack(null)  // Add to backstack so user can navigate back
            .commit()
    }

    override fun navigateToWithoutStackTrace(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.consumerDashFrameLayout, fragment)
            .commit()
    }

    fun onCategoryClicked(view: View) {
        val bundle = Bundle()
        bundle.putString("subCategory", view.tag.toString())
//        val fragment = SubCategory()
        val fragment = SubCatList()
        fragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .replace(R.id.consumerDashFrameLayout, fragment)
            .addToBackStack(null)  // Add to backstack so user can navigate back
            .commit()
    }

    fun onSubCategoryClicked(view: View) {
        val bundle = Bundle()
        bundle.putString("subCategory", view.tag.toString())
        val fragment = SubCatList()
        fragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .replace(R.id.consumerDashFrameLayout, fragment)
            .addToBackStack(null)  // Add to backstack so user can navigate back
            .commit()
    }

    override fun onPaymentSuccess(p0: String?) {
        isPaymentSuccess = true
//        Log.d("PAYMENT_Status_main", "Success ${p0.toString()}")
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        isPaymentSuccess = false
//        Log.d("PAYMENT_Status_main", "Failed ${p1.toString()}")
    }

    fun checkPaymentStatus(): Boolean {
        return isPaymentSuccess
    }

    fun resetPaymentStatus() {
        isPaymentSuccess = false
    }
}