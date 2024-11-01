package com.pycreation.e_commerce.common.productdetails

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.pycreation.e_commerce.MyWidgets
import com.pycreation.e_commerce.R
import com.pycreation.e_commerce.databinding.ActivityProductLifeCycleBinding

class ProductLifeCycle : MyWidgets() {
    private lateinit var binding: ActivityProductLifeCycleBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityProductLifeCycleBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val bundle = Bundle()
        bundle.putString("data", intent.getStringExtra("data"))
        val productDetailed = ProductDetailed()
        productDetailed.arguments = bundle
        navigateToWithoutStackTrace(productDetailed)
    }

    override fun navigateTo(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.productFrameLayout, fragment)
            .addToBackStack(null)  // Add to backstack so user can navigate back
            .commit()
    }

    override fun navigateToWithoutStackTrace(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.productFrameLayout, fragment)
            .commit()
    }
}