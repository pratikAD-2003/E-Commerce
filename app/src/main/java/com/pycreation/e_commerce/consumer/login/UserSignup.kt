package com.pycreation.e_commerce.consumer.login

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.pycreation.e_commerce.consumer.UserAuth
import com.pycreation.e_commerce.consumer.models.User
import com.pycreation.e_commerce.consumer.responseModels.RegisterResponse
import com.pycreation.e_commerce.databinding.FragmentUserSignupBinding
import com.pycreation.e_commerce.retrofit.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class UserSignup : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private lateinit var binding: FragmentUserSignupBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserSignupBinding.inflate(inflater, container, false)

        binding.sendOtpBtnUserAuth.setOnClickListener {
            if (checkValid()) {
                registerUser()
            }
        }

        binding.alreadyHaveAccBtn.setOnClickListener {
            (activity as? UserAuth)?.navigateTo(LoginUser())
        }

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) = UserSignup().apply {
            arguments = Bundle().apply {
                putString(ARG_PARAM1, param1)
                putString(ARG_PARAM2, param2)
            }
        }
    }

    private fun checkValid(): Boolean {
        val email = binding.emailEditUserAuth.text.toString()
        val pass = binding.passwordEditUserAuth.text.toString()
        val cPass = binding.confirmPassEditUserAuth.text.toString()

        if (email.isEmpty() || pass.isEmpty() || cPass.isEmpty()) {
            (activity as? UserAuth)?.showError("Please fill all details...")
            return false
        }
        if (!email.endsWith("@gmail.com")) {
            (activity as? UserAuth)?.showError("Email address should ends with `@gmail.com` ")
            return false
        }
        if (pass != cPass) {
            (activity as? UserAuth)?.showError("Password not matched")
            binding.passwordEditUserAuth.setText("")
            binding.confirmPassEditUserAuth.setText("")
            return false
        }
        return true
    }

    private fun registerUser() {
        (activity as? UserAuth)?.showDialog("Sending OTP...")
        val user = User(
            binding.emailEditUserAuth.text.toString(), binding.passwordEditUserAuth.text.toString(),
            "user",
            true,
            isAddressAdded = false
        )
        val apiService = ApiClient.getApiService()
        if (apiService != null) {
            apiService.registerUser(user)!!.enqueue(object : Callback<RegisterResponse?> {
                override fun onResponse(
                    call: Call<RegisterResponse?>, response: Response<RegisterResponse?>
                ) {
                    if (response.isSuccessful) {
//                        Log.d("REGISTERED-->", response.body().toString())
                        val verifyUserOTP = VerifyUserOTP()
                        val bundle = Bundle()
                        Toast.makeText(requireContext(),"OTP sent successfully!",Toast.LENGTH_SHORT).show()
                        bundle.putString("email", binding.emailEditUserAuth.text.toString())
                        bundle.putString("from","register");
                        verifyUserOTP.arguments = bundle
                        (activity as? UserAuth)?.dismissDialog()
                        (activity as? UserAuth)?.navigateTo(verifyUserOTP)
                    } else {
                        Log.d(
                            "REGISTER_ERROR",
                            "Code: ${response.code()}, Body: ${response.errorBody()?.string()}"
                        )
                        (activity as? UserAuth)?.dismissDialog()
                    }
                }

                override fun onFailure(call: Call<RegisterResponse?>, t: Throwable) {
                    Log.e("REGISTER_ERROR", "Error: ${t.message}", t)
                    (activity as? UserAuth)?.dismissDialog()
                }
            })
        }
    }
}