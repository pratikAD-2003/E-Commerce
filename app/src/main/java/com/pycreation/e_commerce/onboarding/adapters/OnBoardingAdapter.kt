package com.pycreation.e_commerce.onboarding.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.pycreation.e_commerce.onboarding.On1
import com.pycreation.e_commerce.onboarding.On2
import com.pycreation.e_commerce.onboarding.On3
import com.pycreation.e_commerce.onboarding.RoleSelection

class OnBoardingAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> On1()
            1 -> On2()
            2 -> On3()
            else -> RoleSelection()
        }
    }
}