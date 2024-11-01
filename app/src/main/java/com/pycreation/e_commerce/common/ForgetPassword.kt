package com.pycreation.e_commerce.common

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.pycreation.e_commerce.MyWidgets
import com.pycreation.e_commerce.R
import com.pycreation.e_commerce.admin.PartnerAuth
import com.pycreation.e_commerce.common.models.ForgetPassBodyModel
import com.pycreation.e_commerce.consumer.UserAuth
import com.pycreation.e_commerce.consumer.login.VerifyUserOTP
import com.pycreation.e_commerce.consumer.models.User
import com.pycreation.e_commerce.consumer.responseModels.RegisterResponse
import com.pycreation.e_commerce.databinding.FragmentForgetPasswordBinding
import com.pycreation.e_commerce.retrofit.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

const val ARG_PARAM1 = "param1"
const val ARG_PARAM2 = "param2"

class ForgetPassword : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentForgetPasswordBinding
    private var type: String? = ""
    private lateinit var myActivity: MyWidgets


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
        binding = FragmentForgetPasswordBinding.inflate(inflater, container, false)

        type = arguments?.getString("type")
        myActivity =
            if (type.equals("user") || type.equals("register")) (activity as? UserAuth)!! else (activity as? PartnerAuth)!!

        binding.sendOtpBtnForgotPass.setOnClickListener {
            if (checkValid()) {
                forgetPassword()
            }
        }

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ForgetPassword().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun checkValid(): Boolean {
        val email = binding.emailEditForgotPass.text.toString()
        val pass = binding.passwordEditForgotPass.text.toString()
        val cPass = binding.confirmPassEditForgotPass.text.toString()

        if (email.isEmpty() || pass.isEmpty() || cPass.isEmpty()) {
            myActivity.showError("Please fill all details...")
            return false
        }
        if (!email.endsWith("@gmail.com")) {
            myActivity.showError("Email address should ends with `@gmail.com` ")
            return false
        }
        if (pass != cPass) {
            myActivity.showError("Password not matched")
            binding.passwordEditForgotPass.setText("")
            binding.confirmPassEditForgotPass.setText("")
            return false
        }
        return true
    }

    private fun forgetPassword() {
        myActivity.showDialog("Sending OTP...")
        val forgetPassBodyModel = ForgetPassBodyModel(
            binding.emailEditForgotPass.text.toString(),
            binding.confirmPassEditForgotPass.text.toString()
        )

        val apiService = ApiClient.getApiService()
        if (apiService != null) {
            apiService.forgetPassword(forgetPassBodyModel)!!
                .enqueue(object : Callback<RegisterResponse?> {
                    override fun onResponse(
                        call: Call<RegisterResponse?>, response: Response<RegisterResponse?>
                    ) {
                        if (response.isSuccessful) {
//                        Log.d("REGISTERED-->", response.body().toString())
                            val verifyUserOTP = VerifyUserOTP()
                            val bundle = Bundle()
                            Toast.makeText(
                                requireContext(),
                                "OTP sent successfully!",
                                Toast.LENGTH_SHORT
                            ).show()
                            bundle.putString("email", binding.emailEditForgotPass.text.toString())
                            if (type.equals("user")) {
                                bundle.putString("from", "user");
                            } else if (type.equals("partner")) {
                                bundle.putString("from", "partner");
                            }
                            verifyUserOTP.arguments = bundle
                            myActivity.dismissDialog()
                            myActivity.navigateToWithoutStackTrace(verifyUserOTP)
                        } else {
                            Log.d(
                                "REGISTER_ERROR",
                                "Code: ${response.code()}, Body: ${response.errorBody()?.string()}"
                            )
                            myActivity.dismissDialog()
                        }
                    }

                    override fun onFailure(call: Call<RegisterResponse?>, t: Throwable) {
                        Log.e("REGISTER_ERROR", "Error: ${t.message}", t)
                        myActivity.dismissDialog()
                    }
                })
        }
    }
}