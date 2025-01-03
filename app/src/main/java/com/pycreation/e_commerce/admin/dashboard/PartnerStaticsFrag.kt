package com.pycreation.e_commerce.admin.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pycreation.e_commerce.R
import com.pycreation.e_commerce.databinding.FragmentPartnerStaticsBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class PartnerStaticsFrag : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentPartnerStaticsBinding

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
        binding = FragmentPartnerStaticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PartnerStaticsFrag().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}