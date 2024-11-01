package com.pycreation.e_commerce.onboarding

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pycreation.e_commerce.R
import com.pycreation.e_commerce.databinding.FragmentOn1Binding
import com.pycreation.e_commerce.databinding.FragmentOn3Binding
import com.pycreation.e_commerce.onboarding.On1.OnFragmentChangeListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [On3.newInstance] factory method to
 * create an instance of this fragment.
 */
class On3 : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    private lateinit var binding : FragmentOn3Binding
    private var listener: OnFragmentChangeListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Attach the listener to the activity
        if (context is OnFragmentChangeListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentChangeListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOn3Binding.inflate(inflater, container, false)
        // Inflate the layout for this fragment

        binding.on3Pre.setOnClickListener {
            listener?.onChangeFragment(1)
        }

        binding.on3Next.setOnClickListener {
            listener?.onChangeFragment(3)
        }
        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment On3.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            On3().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    interface OnFragmentChangeListener {
        fun onChangeFragment(position: Int)
    }
}