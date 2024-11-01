package com.pycreation.e_commerce.admin

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.pycreation.e_commerce.MyWidgets
import com.pycreation.e_commerce.R
import com.pycreation.e_commerce.admin.login.PartnerRegister
import com.pycreation.e_commerce.databinding.ActivityPartnerAuthBinding

class PartnerAuth : MyWidgets() {
    private lateinit var binding : ActivityPartnerAuthBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityPartnerAuthBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.partnerAuthFragment, PartnerRegister())
                .commit()
        }
    }

    override fun navigateTo(fragment : Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.partnerAuthFragment, fragment)
            .addToBackStack(null)  // Add to backstack so user can navigate back
            .commit()
    }

    override fun navigateToWithoutStackTrace(fragment : Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.partnerAuthFragment, fragment)
            .commit()
    }
}