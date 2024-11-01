package com.pycreation.e_commerce.common.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import com.pycreation.e_commerce.R
import com.pycreation.e_commerce.databinding.FragmentSearchBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SearchFrag : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentSearchBinding

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
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        binding.keywordSearchFrag.requestFocus()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finish()
        }

        binding.backFromSearchFrag.setOnClickListener {
            (activity as AppCompatActivity).finish()
        }

        binding.searchBtnSearchFrag.setOnClickListener {
            if (binding.keywordSearchFrag.text.isNotEmpty()) {
                val bundle = Bundle()
                bundle.putString("keyword", binding.keywordSearchFrag.text.toString())
                val productListFrag = ProductList()
                productListFrag.arguments = bundle
                (activity as SearchProList?)?.navigateTo(productListFrag)
            }
        }

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchFrag().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}