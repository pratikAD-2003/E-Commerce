package com.pycreation.e_commerce.common.search

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.pycreation.e_commerce.MyWidgets
import com.pycreation.e_commerce.R
import com.pycreation.e_commerce.admin.dashboard.PartnerDashFrag
import com.pycreation.e_commerce.databinding.ActivitySearchProListBinding

class SearchProList : MyWidgets() {
    private lateinit var binding: ActivitySearchProListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySearchProListBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.searchFragmentLayout, SearchFrag())
                .commit()
        }
    }

    override fun navigateToWithoutStackTrace(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.searchFragmentLayout, fragment)
            .commit()
    }

    override fun navigateTo(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.searchFragmentLayout, fragment)
            .addToBackStack(null)
            .commit()
    }
}