package com.pycreation.e_commerce.consumer.address.activities

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.pycreation.e_commerce.R
import com.pycreation.e_commerce.UserSharedPref
import com.pycreation.e_commerce.consumer.address.adaptors.AddressAdapter
import com.pycreation.e_commerce.consumer.address.model.req.PostAddressReqModel
import com.pycreation.e_commerce.consumer.address.model.req.RemoveAddressModel
import com.pycreation.e_commerce.consumer.address.model.req.UpdateAddressReqModel
import com.pycreation.e_commerce.consumer.address.model.res.AddressListModelResModel
import com.pycreation.e_commerce.consumer.dashboard.ConsumerDashboard
import com.pycreation.e_commerce.consumer.responseModels.RegisterResponse
import com.pycreation.e_commerce.databinding.FragmentAddressListBinding
import com.pycreation.e_commerce.retrofit.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AddressList : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentAddressListBinding
    private var isForUpdate: Boolean = false

    private lateinit var _id: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private var addressType: String = "Home"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddressListBinding.inflate(inflater, container, false)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().supportFragmentManager.popBackStack()
        }
        setupActionBar()
        binding.addressRecyclerviewAddresses.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        binding.addNewAddressBtnAddresses.setOnClickListener {
            isForUpdate = false
            addressType = "Home"
            binding.fullNameAddress.setText("")
            binding.pinCodeAddress.setText("")
            binding.phoneNumberAddress.setText("")
            binding.stateAddress.setText("")
            binding.cityAddress.setText("")
            binding.areaAddress.setText("")
            binding.addAddressTextAddressList.text = "Add"
            binding.addNewAddressLayoutAddressList.visibility = View.GONE
            binding.newAddressLayoutAddress.visibility = View.VISIBLE
        }

        binding.cancelBtnAddress.setOnClickListener {
            isForUpdate = false
            binding.addNewAddressLayoutAddressList.visibility = View.VISIBLE
            binding.newAddressLayoutAddress.visibility = View.GONE
        }

        binding.addAddressBtnAddress.setOnClickListener {
            if (checkValidation()) {
                if (isForUpdate) {
                    val fullName = binding.fullNameAddress.text.toString()
                    val phoneNumber = binding.phoneNumberAddress.text.toString()
                    val pinCode = binding.pinCodeAddress.text.toString()
                    val state = binding.stateAddress.text.toString()
                    val city = binding.cityAddress.text.toString()
                    val area = binding.areaAddress.text.toString()
                    val updateAddressReqModel = UpdateAddressReqModel(
                        _id,
                        area,
                        city,
                        fullName,
                        phoneNumber,
                        pinCode,
                        state,
                        addressType,
                        UserSharedPref(requireContext()).getToken()
                    )
                    updateAddress(updateAddressReqModel)
                } else {
                    binding.addAddressTextAddressList.text = "Add"
                    addNewAddress()
                    isForUpdate = false
                    addressType = "Home"
                    binding.fullNameAddress.setText("")
                    binding.pinCodeAddress.setText("")
                    binding.stateAddress.setText("")
                    binding.cityAddress.setText("")
                    binding.areaAddress.setText("")
                }
            }
        }
        getAddressList()
        setupSelectAddressType()
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddressList().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    private fun setupActionBar() {
        (activity as AppCompatActivity).setSupportActionBar(binding.savedAddressesToolbar)
        val actionBar = (activity as AppCompatActivity).supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
        }
        binding.savedAddressesToolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun updateAddress(updateAddressReqModel: UpdateAddressReqModel) {
        (activity as ConsumerDashboard?)?.showDialog("Wait...")
        val apiService = ApiClient.getApiService()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                apiService?.updateAddress(updateAddressReqModel)
                    ?.enqueue(object : Callback<RegisterResponse?> {
                        override fun onResponse(
                            call: Call<RegisterResponse?>,
                            response: Response<RegisterResponse?>
                        ) {
                            if (response.isSuccessful) {
                                Log.d("REMOVE_ADDRESS", response.body().toString())
                                binding.newAddressLayoutAddress.visibility = View.GONE
                                binding.addNewAddressLayoutAddressList.visibility = View.VISIBLE
                                (activity as ConsumerDashboard?)?.showError(response.body()!!.message)
                                (activity as ConsumerDashboard?)?.dismissDialog()
                                isForUpdate = false
                                getAddressList()
                            } else {
                                binding.newAddressLayoutAddress.visibility = View.GONE
                                binding.addNewAddressLayoutAddressList.visibility = View.VISIBLE
                                Log.d("REMOVE_ADDRESS", response.errorBody().toString())
                                (activity as ConsumerDashboard?)?.dismissDialog()
                                isForUpdate = false
                            }
                        }

                        override fun onFailure(
                            call: Call<RegisterResponse?>,
                            t: Throwable
                        ) {
                            binding.newAddressLayoutAddress.visibility = View.GONE
                            binding.addNewAddressLayoutAddressList.visibility = View.VISIBLE
                            Log.d("REMOVE_ADDRESS", t.message.toString())
                            isForUpdate = false
                            (activity as ConsumerDashboard?)?.dismissDialog()
                        }
                    })
            } catch (e: Exception) {
                Log.d("REMOVE_ADDRESS", e.message.toString())
                (activity as ConsumerDashboard?)?.dismissDialog()
            }
        }
    }

    private fun removeAddress(_id: String) {
        (activity as ConsumerDashboard?)?.showDialog("Wait...")
        val apiService = ApiClient.getApiService()
        val removeAddressModel =
            RemoveAddressModel(_id, UserSharedPref(requireContext()).getToken())
        CoroutineScope(Dispatchers.IO).launch {
            try {
                apiService?.removeAddress(removeAddressModel)
                    ?.enqueue(object : Callback<RegisterResponse?> {
                        override fun onResponse(
                            call: Call<RegisterResponse?>,
                            response: Response<RegisterResponse?>
                        ) {
                            if (response.isSuccessful) {
                                Log.d("REMOVE_ADDRESS", response.body().toString())
                                (activity as ConsumerDashboard?)?.showError(response.body()!!.message)
                                (activity as ConsumerDashboard?)?.dismissDialog()
                                getAddressList()
                            } else {
                                Log.d("REMOVE_ADDRESS", response.errorBody().toString())
                                (activity as ConsumerDashboard?)?.dismissDialog()
                            }
                        }

                        override fun onFailure(
                            call: Call<RegisterResponse?>,
                            t: Throwable
                        ) {
                            Log.d("REMOVE_ADDRESS", t.message.toString())
                            (activity as ConsumerDashboard?)?.dismissDialog()
                        }
                    })
            } catch (e: Exception) {
                Log.d("REMOVE_ADDRESS", e.message.toString())
                (activity as ConsumerDashboard?)?.dismissDialog()
            }
        }
    }

    private fun getAddressList() {
        val apiService = ApiClient.getApiService()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                apiService?.getAddresses(UserSharedPref(requireContext()).getToken())
                    ?.enqueue(object : Callback<AddressListModelResModel?> {
                        override fun onResponse(
                            call: Call<AddressListModelResModel?>,
                            response: Response<AddressListModelResModel?>
                        ) {
                            if (response.isSuccessful) {
                                binding.addNewAddressLayoutAddressList.visibility =
                                    View.VISIBLE
                                Log.d("ADDRESS_LIST", response.body().toString())
                                val adapter = AddressAdapter(
                                    requireContext(),
                                    response.body()!!,
                                    onEditClicked = {
                                        isForUpdate = true
                                        _id = it._id
                                        binding.newAddressLayoutAddress.visibility = View.VISIBLE
                                        binding.addNewAddressLayoutAddressList.visibility =
                                            View.GONE
                                        binding.fullNameAddress.setText(it.fullName)
                                        binding.phoneNumberAddress.setText(it.phoneNumber.toString())
                                        if (it.type == "Home") {
                                            buttonSelected(binding.homeTypeImgAddress)
                                            buttonUnSelected(binding.workTypeImgAddress)
                                        } else {
                                            buttonSelected(binding.workTypeImgAddress)
                                            buttonUnSelected(binding.homeTypeImgAddress)
                                        }
                                        addressType = it.type
                                        binding.pinCodeAddress.setText(it.pinCode.toString())
                                        binding.stateAddress.setText(it.state)
                                        binding.cityAddress.setText(it.city)
                                        binding.areaAddress.setText(it.area)
                                        binding.addAddressTextAddressList.text = "Update"
                                    },
                                    onRemovedItem = {
                                        removeAddress(it)
                                    })
                                binding.addressRecyclerviewAddresses.adapter = adapter
                            } else {
                                binding.addNewAddressLayoutAddressList.visibility =
                                    View.VISIBLE
                                Log.d("ADDRESS_LIST", response.errorBody().toString())
                            }
                        }

                        override fun onFailure(
                            call: Call<AddressListModelResModel?>,
                            t: Throwable
                        ) {
                            binding.addNewAddressLayoutAddressList.visibility =
                                View.VISIBLE
                            Log.d("ADDRESS_LIST", t.message.toString())
                        }

                    })
            } catch (e: Exception) {
                binding.addNewAddressLayoutAddressList.visibility =
                    View.VISIBLE
                Log.d("ADDRESS_LIST", e.message.toString())
            }
        }
    }

    private fun addNewAddress() {
        val fullName = binding.fullNameAddress.text.toString()
        val phoneNumber = binding.phoneNumberAddress.text.toString()
        val pinCode = binding.pinCodeAddress.text.toString()
        val state = binding.stateAddress.text.toString()
        val city = binding.cityAddress.text.toString()
        val area = binding.areaAddress.text.toString()

        val apiService = ApiClient.getApiService()
        val postAddressReqModel = PostAddressReqModel(
            area,
            city,
            fullName,
            phoneNumber,
            pinCode,
            state,
            addressType,
            UserSharedPref(requireContext()).getToken()
        )
        CoroutineScope(Dispatchers.IO).launch {
            try {
                apiService?.addAddress(postAddressReqModel)
                    ?.enqueue(object : Callback<RegisterResponse?> {
                        override fun onResponse(
                            call: Call<RegisterResponse?>,
                            response: Response<RegisterResponse?>
                        ) {
                            if (response.isSuccessful) {
                                Log.d("ADD_ADDRESS", response.body().toString())
                                binding.newAddressLayoutAddress.visibility = View.GONE
                                binding.addNewAddressBtnAddresses.visibility = View.VISIBLE
                                getAddressList()

                            } else {
                                Log.d("ADD_ADDRESS", response.errorBody().toString())
                            }
                        }

                        override fun onFailure(
                            call: Call<RegisterResponse?>,
                            t: Throwable
                        ) {
                            Log.d("ADD_ADDRESS", t.message.toString())
                        }

                    })
            } catch (e: Exception) {
                Log.d("ADD_ADDRESS", e.message.toString())
            }
        }
    }

    private fun checkValidation(): Boolean {
        val fullName = binding.fullNameAddress.text.toString()
        val phoneNumber = binding.phoneNumberAddress.text.toString()
        val pinCode = binding.pinCodeAddress.text.toString()
        val state = binding.stateAddress.text.toString()
        val city = binding.cityAddress.text.toString()
        val area = binding.areaAddress.text.toString()

        if (fullName.isEmpty() || phoneNumber.isEmpty() || pinCode.isEmpty() || state.isEmpty() || city.isEmpty() || area.isEmpty()) {
            (activity as ConsumerDashboard?)?.showError("Please fill all details!")
            return false
        }
        if (phoneNumber.length != 10) {
            (activity as ConsumerDashboard?)?.showError("Invalid phone number!")
            return false
        }
        return true;
    }

    private fun setupSelectAddressType() {
        binding.homeTypeAddress.setOnClickListener {
            buttonSelected(binding.homeTypeImgAddress)
            buttonUnSelected(binding.workTypeImgAddress)
            addressType = "Home"
        }

        binding.workTypeAddress.setOnClickListener {
            buttonSelected(binding.workTypeImgAddress)
            buttonUnSelected(binding.homeTypeImgAddress)
            addressType = "Work"
        }
    }

    private fun buttonSelected(imageView: ImageView) {
        imageView.setImageResource(R.drawable.oval_red)
        imageView.setBackgroundDrawable(resources.getDrawable(R.drawable.black_outlined))
    }

    private fun buttonUnSelected(imageView: ImageView) {
        imageView.setImageResource(R.drawable.white_oval)
        imageView.setBackgroundDrawable(resources.getDrawable(R.drawable.grey_outlined))
    }
}