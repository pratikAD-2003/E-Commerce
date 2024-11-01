package com.pycreation.e_commerce.admin.listening

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.pycreation.e_commerce.MyWidgets
import com.pycreation.e_commerce.R
import com.pycreation.e_commerce.common.productdetails.ProductDetailed
import com.pycreation.e_commerce.databinding.ActivityPartnerProLisBinding

class PartnerProLisActivity : MyWidgets() {
    private lateinit var binding: ActivityPartnerProLisBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityPartnerProLisBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        navigateTo(PartnerProductPostFrag())
//        navigateTo(ProductDetailed())
    }

    override fun navigateTo(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.partnerProductListFrameLayout, fragment)
            .addToBackStack(null)  // Add to backstack so user can navigate back
            .commit()
    }

    override fun navigateToWithoutStackTrace(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.partnerProductListFrameLayout, fragment)
            .commit()
    }
}