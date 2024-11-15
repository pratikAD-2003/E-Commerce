package com.pycreation.e_commerce.consumer.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.pycreation.e_commerce.MyWidgets
import com.pycreation.e_commerce.R
import com.pycreation.e_commerce.UserSharedPref
import com.pycreation.e_commerce.admin.PartnerAuth
import com.pycreation.e_commerce.admin.login.PartnerLogin
import com.pycreation.e_commerce.consumer.UserAuth
import com.pycreation.e_commerce.consumer.UserFirebase
import com.pycreation.e_commerce.consumer.dashboard.ConsumerDashboard
import com.pycreation.e_commerce.consumer.dashboard.tabs.Account
import com.pycreation.e_commerce.consumer.models.User
import com.pycreation.e_commerce.consumer.models.UserFirebaseModel
import com.pycreation.e_commerce.consumer.models.UserOtpModel
import com.pycreation.e_commerce.consumer.responseModels.RegisterResponse
import com.pycreation.e_commerce.consumer.responseModels.VerifyEmailResponseModel
import com.pycreation.e_commerce.databinding.FragmentVerifyUserOTPBinding
import com.pycreation.e_commerce.retrofit.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class VerifyUserOTP : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private lateinit var binding: FragmentVerifyUserOTPBinding
    private var type: String? = ""
    private lateinit var myActivity: MyWidgets

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVerifyUserOTPBinding.inflate(inflater, container, false)

        val email = arguments?.getString("email")
        type = arguments?.getString("from")

        myActivity =
            if (type.equals("user") || type.equals("register")) (activity as? UserAuth)!! else if (type.equals(
                    "from_user_dash"
                )
            ) (activity as? ConsumerDashboard)!! else (activity as? PartnerAuth)!!

        if (email != null) {
            binding.emailTextUserAuth.text =
                "Please enter valid 6 digits One Time Password (OTP) on $email"
        }

        changeCursorNext(binding.otp1UserAuth, binding.otp2UserAuth)
        changeCursorNext(binding.otp2UserAuth, binding.otp3UserAuth)
        changeCursorNext(binding.otp3UserAuth, binding.otp4UserAuth)
        changeCursorNext(binding.otp4UserAuth, binding.otp5UserAuth)
        changeCursorNext(binding.otp5UserAuth, binding.otp6UserAuth)

        binding.verifyOtpButton.setOnClickListener {
            val otp =
                binding.otp1UserAuth.text.toString() + binding.otp2UserAuth.text.toString() + binding.otp3UserAuth.text.toString() + binding.otp4UserAuth.text.toString() + binding.otp5UserAuth.text.toString() + binding.otp6UserAuth.text.toString()
            if (otp.length < 6) {
                myActivity.showError("Please enter valid OTP.")
            } else {
                verifyOtp(email!!, otp)
            }
        }

        binding.resendBtnVerifyUserOtp.setOnClickListener {

        }

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            VerifyUserOTP().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun changeCursorNext(editText: EditText, editText2: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (editText.text.toString().length == 1) {
                    editText2.requestFocus()
                }
            }

            override fun afterTextChanged(editable: Editable) {
            }
        })
    }

    private fun verifyOtp(email: String, otp: String) {
        myActivity.showDialog("Verifying...")
        val apiService = ApiClient.getApiService()
        val user = UserOtpModel(
            email, otp
        )
        if (apiService != null) {
            if (type.equals("register")) {
                apiService.verifyEmail(user)!!
                    .enqueue(object : Callback<VerifyEmailResponseModel?> {
                        override fun onResponse(
                            call: Call<VerifyEmailResponseModel?>,
                            response: Response<VerifyEmailResponseModel?>
                        ) {
                            if (response.isSuccessful) {
                                myActivity.dismissDialog()
                                UserSharedPref(requireContext()).saveLoginStatus(
                                    "user",
                                    response.body()!!.token,
                                    docUploaded = true,
                                    isAddressAdded =  false
                                )
                                Toast.makeText(
                                        requireContext(),
                                "Registered Successfully!",
                                Toast.LENGTH_SHORT
                                ).show()
                                val intent = Intent(requireContext(), ConsumerDashboard::class.java)
                                myActivity.startActivity(intent)
                                myActivity.finish()
                            } else {
                                Log.e(
                                    "LOGIN_ERROR",
                                    "Code: ${response.code()}, Body: ${
                                        response.errorBody()?.string()
                                    }"
                                )
                                myActivity.dismissDialog()
                                response.errorBody()?.string()?.let { myActivity.showError(it) }
                            }
                        }

                        override fun onFailure(
                            call: Call<VerifyEmailResponseModel?>,
                            t: Throwable
                        ) {
                            Log.e("LOGIN_ERROR", "Error: ${t.message}", t)
                            myActivity.dismissDialog()
                        }
                    })
            } else if (type.equals("user") || type.equals("partner") || type.equals("from_user_dash")) {
                apiService.verifyEmailForgetPassword(user)!!
                    .enqueue(object : Callback<RegisterResponse?> {
                        override fun onResponse(
                            call: Call<RegisterResponse?>,
                            response: Response<RegisterResponse?>
                        ) {
                            if (response.isSuccessful) {
                                myActivity.dismissDialog()
                                myActivity.showError("Password has been changed!")
                                if (type.equals("user")) {
                                    myActivity.navigateToWithoutStackTrace(LoginUser())
                                } else if (type.equals("from_user_dash")) {
                                    requireActivity().supportFragmentManager.popBackStack(
                                        null,
                                        FragmentManager.POP_BACK_STACK_INCLUSIVE
                                    )
                                    (activity as ConsumerDashboard?)?.setAccountIcon()
                                    (activity as ConsumerDashboard?)?.navigateToWithoutStackTrace(
                                        Account()
                                    )
                                } else {
                                    myActivity.navigateToWithoutStackTrace(PartnerLogin())
                                }
                            } else {
                                Log.e(
                                    "LOGIN_ERROR",
                                    "Code: ${response.code()}, Body: ${
                                        response.errorBody()?.string()
                                    }"
                                )
                                response.errorBody()?.string()?.let { myActivity.showError(it) }
                                myActivity.dismissDialog()
                            }
                        }

                        override fun onFailure(
                            call: Call<RegisterResponse?>,
                            t: Throwable
                        ) {
                            Log.e("LOGIN_ERROR", "Error: ${t.message}", t)
                            myActivity.dismissDialog()
                        }
                    })
            }
        }
    }
}