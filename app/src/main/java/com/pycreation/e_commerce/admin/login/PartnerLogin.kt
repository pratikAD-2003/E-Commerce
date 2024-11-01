package com.pycreation.e_commerce.admin.login

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pycreation.e_commerce.R
import com.pycreation.e_commerce.UserSharedPref
import com.pycreation.e_commerce.admin.PartnerAuth
import com.pycreation.e_commerce.admin.dashboard.PartnerDashboard
import com.pycreation.e_commerce.common.ForgetPassword
import com.pycreation.e_commerce.consumer.UserAuth
import com.pycreation.e_commerce.consumer.dashboard.ConsumerDashboard
import com.pycreation.e_commerce.consumer.login.UserSignup
import com.pycreation.e_commerce.consumer.models.User
import com.pycreation.e_commerce.consumer.responseModels.LoginResModel
import com.pycreation.e_commerce.databinding.FragmentPartnerLoginBinding
import com.pycreation.e_commerce.retrofit.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class PartnerLogin : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentPartnerLoginBinding

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
        binding = FragmentPartnerLoginBinding.inflate(inflater, container, false)

        binding.didNotHavePartnerAccBtn.setOnClickListener {
            (activity as? PartnerAuth?)?.navigateToWithoutStackTrace(PartnerRegister())
        }

        binding.forgetPasswordBtnPartnerLoginUser.setOnClickListener {
            val forgetPassFra = ForgetPassword()
            val bundle = Bundle()
            bundle.putString("type", "partner")
            forgetPassFra.arguments = bundle
            (activity as? PartnerAuth?)?.navigateTo(forgetPassFra)
        }

        binding.loginPartnerBtnUser.setOnClickListener {
            if (checkValid()) {
                userLogin()
            }
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PartnerLogin().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun userLogin() {
        (activity as? PartnerAuth)?.showDialog("Login...")
        val apiService = ApiClient.getApiService()

        if (apiService != null) {
            val email = binding.emailPartnerLogin.text.toString()
            val pass = binding.passwordPartnerLogin.text.toString()
            val user = User(email, pass)
            apiService.loginUser(user)?.enqueue(object : Callback<LoginResModel?> {
                override fun onResponse(
                    call: Call<LoginResModel?>,
                    response: Response<LoginResModel?>
                ) {
                    if (response.isSuccessful) {
                        (activity as? PartnerAuth)?.dismissDialog()
                        if (response.body()!!.type == "user") {
//                            (activity as? UserAuth)?.showError("Login Successfully")
//                            UserSharedPref(requireContext()).saveLoginStatus(
//                                "user",
//                                response.body()!!.token,
//                                true,
//                                response.body()!!.isAddressAdded
//                            )
//                            val intent = Intent(requireContext(), ConsumerDashboard::class.java)
//                            (activity as? UserAuth)?.startActivity(intent)
//                            (activity as? UserAuth)?.finish()
                            (activity as? PartnerAuth)?.showError("Invalid Credentials for User Type!")
                        } else {
                            UserSharedPref(requireContext()).saveLoginStatus(
                                "partner",
                                response.body()!!.token,
                                response.body()!!.docUploaded,
                                response.body()!!.isAddressAdded
                            )
                            if (response.body()!!.docUploaded) {
                                val intent = Intent(requireContext(), PartnerDashboard::class.java)
                                (activity as? PartnerAuth)?.startActivity(intent)
                                (activity as? PartnerAuth)?.finish()
                            } else {
//                                val intent = Intent(requireContext(), PartnerRegister::class.java)
//                                (activity as? PartnerAuth)?.startActivity(intent)
//                                (activity as? PartnerAuth)?.finish()
                            }
                        }
                    } else {
                        (activity as? PartnerAuth)?.dismissDialog()
                        (activity as? PartnerAuth)?.showError(response.errorBody()!!.string())
                    }
                }

                override fun onFailure(call: Call<LoginResModel?>, t: Throwable) {
                    (activity as? PartnerAuth)?.dismissDialog()
                }
            })
        }
    }

    private fun checkValid(): Boolean {
        val email = binding.emailPartnerLogin.text.toString()
        val pass = binding.passwordPartnerLogin.text.toString()

        if (email.isEmpty() || pass.isEmpty()) {
            (activity as? PartnerAuth)?.showError("Please fill all details...")
            return false
        }
        if (!email.endsWith("@gmail.com")) {
            (activity as? PartnerAuth)?.showError("Email address should ends with `@gmail.com` ")
            return false
        }
        return true
    }
}