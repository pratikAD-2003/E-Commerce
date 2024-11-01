package com.pycreation.e_commerce.onboarding

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.pycreation.e_commerce.MyWidgets
import com.pycreation.e_commerce.UserSharedPref
import com.pycreation.e_commerce.admin.PartnerAuth
import com.pycreation.e_commerce.admin.dashboard.PartnerDashboard
import com.pycreation.e_commerce.consumer.UserAuth
import com.pycreation.e_commerce.consumer.dashboard.ConsumerDashboard
import com.pycreation.e_commerce.databinding.ActivityOnBoardingBinding
import com.pycreation.e_commerce.onboarding.adapters.OnBoardingAdapter

class OnBoardingActivity : MyWidgets(), On1.OnFragmentChangeListener, On2.OnFragmentChangeListener,
    On3.OnFragmentChangeListener {
    private lateinit var binding: ActivityOnBoardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val adapter = OnBoardingAdapter(this)

        binding.onboardingViewpager.adapter = adapter
    }

    override fun onChangeFragment(position: Int) {
        binding.onboardingViewpager.setCurrentItem(position, true)
    }

    override fun onStart() {
        super.onStart()
//        Log.d("SAVED_INFO",UserSharedPref(this@OnBoardingActivity).getToken())
        if (UserSharedPref(this@OnBoardingActivity).getUserType() == "user") {
            startActivity(Intent(this@OnBoardingActivity, ConsumerDashboard::class.java))
            finish()
        } else if (UserSharedPref(this@OnBoardingActivity).getUserType() == "partner") {
            if (UserSharedPref(this@OnBoardingActivity).getDocStatus()) {
                startActivity(Intent(this@OnBoardingActivity, PartnerDashboard::class.java))
                finish()
            }else{
//                startActivity(Intent(this@OnBoardingActivity, PartnerDashboard::class.java))
//                finish()
            }
        }
    }
}