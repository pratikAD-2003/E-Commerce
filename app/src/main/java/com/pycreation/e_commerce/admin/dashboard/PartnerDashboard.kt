package com.pycreation.e_commerce.admin.dashboard

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.pycreation.e_commerce.MyWidgets
import com.pycreation.e_commerce.R
import com.pycreation.e_commerce.admin.listening.PartnerProLisActivity
import com.pycreation.e_commerce.admin.listening.PartnerProductPostFrag
import com.pycreation.e_commerce.admin.login.PartnerRegister
import com.pycreation.e_commerce.admin.models.productModel.ProductModel
import com.pycreation.e_commerce.consumer.responseModels.RegisterResponse
import com.pycreation.e_commerce.databinding.ActivityPartnerDashboardBinding
import com.pycreation.e_commerce.retrofit.ApiClient
import com.pycreation.e_commerce.retrofit.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PartnerDashboard : MyWidgets() {
    private lateinit var binding: ActivityPartnerDashboardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityPartnerDashboardBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.partnerDashFrameLayout, PartnerDashFrag())
                .commit()
        }

        binding.bottomNavViewPartnerDash.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.partDash -> {
                    navigateToWithoutStackTrace(PartnerDashFrag())
                    true
                }

                R.id.partProfile -> {
                    navigateToWithoutStackTrace(PartnerStaticsFrag())
                    true
                }

                else -> false
            }
        }

        binding.addProductPartnerDash.setOnClickListener {
            startActivity(Intent(this@PartnerDashboard,PartnerProLisActivity::class.java))
        }
    }


    override fun navigateTo(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.partnerDashFrameLayout, fragment)
            .addToBackStack(null)  // Add to backstack so user can navigate back
            .commit()
    }

    override fun navigateToWithoutStackTrace(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.partnerDashFrameLayout, fragment)
            .commit()
    }
}