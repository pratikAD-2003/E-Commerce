package com.pycreation.e_commerce.consumer

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.pycreation.e_commerce.MyWidgets
import com.pycreation.e_commerce.R
import com.pycreation.e_commerce.admin.PartnerAuth
import com.pycreation.e_commerce.admin.login.PartnerRegister
import com.pycreation.e_commerce.consumer.login.UserSignup
import com.pycreation.e_commerce.databinding.ActivityUserAuthBinding

class UserAuth : MyWidgets() {

    private lateinit var binding: ActivityUserAuthBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityUserAuthBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.userAuthFrameLayout, UserSignup())
                .commit()
        }
    }

    override fun navigateTo(fragment : Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.userAuthFrameLayout, fragment)
            .addToBackStack(null)  // Add to backstack so user can navigate back
            .commit()
    }

    override fun navigateToWithoutStackTrace(fragment : Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.userAuthFrameLayout, fragment)
            .commit()
    }
}