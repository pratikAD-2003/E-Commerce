package com.pycreation.e_commerce.admin.login

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.pycreation.e_commerce.R
import com.pycreation.e_commerce.UserSharedPref
import com.pycreation.e_commerce.admin.PartnerAuth
import com.pycreation.e_commerce.admin.dashboard.PartnerDashboard
import com.pycreation.e_commerce.admin.models.PartnerModel
import com.pycreation.e_commerce.admin.models.UpdateDocStatusModel
import com.pycreation.e_commerce.consumer.UserAuth
import com.pycreation.e_commerce.consumer.UserFirebase
import com.pycreation.e_commerce.consumer.login.LoginUser
import com.pycreation.e_commerce.consumer.login.VerifyUserOTP
import com.pycreation.e_commerce.consumer.models.User
import com.pycreation.e_commerce.consumer.models.UserFirebaseModel
import com.pycreation.e_commerce.consumer.models.UserOtpModel
import com.pycreation.e_commerce.consumer.responseModels.RegisterResponse
import com.pycreation.e_commerce.consumer.responseModels.VerifyEmailResponseModel
import com.pycreation.e_commerce.databinding.FragmentPartnerRegisterBinding
import com.pycreation.e_commerce.retrofit.ApiClient
import com.pycreation.e_commerce.retrofit.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class PartnerRegister : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentPartnerRegisterBinding
    private lateinit var imagePickerLauncher1: ActivityResultLauncher<Intent>
    private lateinit var imagePickerLauncher2: ActivityResultLauncher<Intent>

    private var governmentIssuedIdUri: Uri? = null
    private var businessRegiDocUri: Uri? = null
    private var isEmailVerified = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPartnerRegisterBinding.inflate(inflater, container, false)

        imagePickerLauncher1 =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    // Get the Uri of the selected image
                    val data: Intent? = result.data
                    governmentIssuedIdUri = data?.data!!
                    governmentIssuedIdUri.let {
                        // Load the image into ImageView (you can use Glide or Picasso if needed)
                        binding.removeGoverIssuedPicPartnerRegi.visibility = View.VISIBLE
                        binding.governIssuedPicPartnerRegi.setImageResource(R.drawable.baseline_done_all_24)
                        binding.governIssuedPicPartnerRegiText.text = "Selected"
                    }
                }
            }

        imagePickerLauncher2 =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    // Get the Uri of the selected image
                    val data: Intent? = result.data
                    businessRegiDocUri = data?.data!!
                    businessRegiDocUri.let {
                        // Load the image into ImageView (you can use Glide or Picasso if needed)
                        binding.removeBusiRegDocPicPartnerRegi.visibility = View.VISIBLE
                        binding.busiRegDocImagePartnerRegi.setImageResource(R.drawable.baseline_done_all_24)
                        binding.busiRegDocImagePartnerRegiText.text = "Selected"
                    }
                }
            }

        binding.selectGoverIDPartnerRegi.setOnClickListener {
            requestPermissions(true)
        }

        binding.selectBusinessProofDocPartnerRegi.setOnClickListener {
            requestPermissions(false)
        }

        binding.removeGoverIssuedPicPartnerRegi.setOnClickListener {
            governmentIssuedIdUri = null
            binding.governIssuedPicPartnerRegi.setImageResource(R.drawable.id_proof)
            binding.removeGoverIssuedPicPartnerRegi.visibility = View.GONE
            binding.governIssuedPicPartnerRegiText.text = "Government-issued ID\n"
        }

        binding.removeBusiRegDocPicPartnerRegi.setOnClickListener {
            businessRegiDocUri = null
            binding.busiRegDocImagePartnerRegi.setImageResource(R.drawable.govt_id)
            binding.removeBusiRegDocPicPartnerRegi.visibility = View.GONE
            binding.busiRegDocImagePartnerRegiText.text = "Business registration document"
        }

        changeCursorNext(binding.otp1PartnerRegi, binding.otp2PartnerRegi)
        changeCursorNext(binding.otp2PartnerRegi, binding.otp3PartnerRegi)
        changeCursorNext(binding.otp3PartnerRegi, binding.otp4PartnerRegi)
        changeCursorNext(binding.otp4PartnerRegi, binding.otp5PartnerRegi)
        changeCursorNext(binding.otp5PartnerRegi, binding.otp6PartnerRegi)


        binding.sendOTPPartnerButton.setOnClickListener {
            if (checkEmailValidation()) {
                verifyEmail()
            }
        }

        binding.verifyOtpPartnerButton.setOnClickListener {
            val otp =
                binding.otp1PartnerRegi.text.toString() + binding.otp2PartnerRegi.text.toString() + binding.otp3PartnerRegi.text.toString() + binding.otp4PartnerRegi.text.toString() + binding.otp5PartnerRegi.text.toString() + binding.otp6PartnerRegi.text.toString()
            if (otp.length < 6) {
                (activity as? UserAuth)?.showError("Please enter valid OTP.")
            } else {
                verifyOtp(otp)
            }
        }

        binding.confirmRegistrationBtnPartnerRegis.setOnClickListener {
            if (checkValidation()) {
                uploadPartnerDetails()
            }
        }

        binding.alreadyHaveAccPartnerRegis.setOnClickListener {
//            (activity as? PartnerAuth)?.navigateTo(LoginUser())
            (activity as? PartnerAuth)?.navigateTo(PartnerLogin())
        }

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) = PartnerRegister().apply {
            arguments = Bundle().apply {
                putString(ARG_PARAM1, param1)
                putString(ARG_PARAM2, param2)
            }
        }
    }


    private fun checkValidation(): Boolean {
        val fullName = binding.fullNamePartnerRegi.text.toString()
        val phoneNumber = binding.phoneNumberPartnerRegi.text.toString()

        val businessName = binding.businessNamePartnerRegi.text.toString()
        val businessRegistrationNumber =
            binding.businessRegistrationNumberPartnerRegi.text.toString()
        val gstin = binding.gstinNumberPartnerRegi.text.toString()
        val fullAddress = binding.businessAddressPartnerRegi.text.toString()

        val bankAccHolderName = binding.bankAccHolderNamePartnerRegi.text.toString()
        val backAccNumber = binding.bankAccNumberPartnerRegi.text.toString()
        val bankName = binding.bankNamePartnerRegi.text.toString()
        val ifsc = binding.ifscCodePartnerRegi.text.toString()

        val isConditionAccepted = binding.acceptConditionsPartnerRegis.isChecked

        if (fullName.isEmpty() || phoneNumber.isEmpty()) {
            (activity as? PartnerAuth)?.showError("Please enter all details.")
            return false
        }

        if (phoneNumber.length != 10) {
            (activity as? PartnerAuth)?.showError("Phone number must be 10 digits.")
            return false
        }

        if (businessName.isEmpty() || businessRegistrationNumber.isEmpty() || gstin.isEmpty() || fullAddress.isEmpty()) {
            (activity as? PartnerAuth)?.showError("Please enter all details.")
            return false
        }
        if (gstin.length != 15) {
            (activity as? PartnerAuth)?.showError("GSTIN number must be 15 digits.")
            return false
        }
        if (bankAccHolderName.isEmpty() || backAccNumber.isEmpty() || bankName.isEmpty() || ifsc.isEmpty()) {
            (activity as? PartnerAuth)?.showError("Please enter all details.")
            return false
        }

        if (governmentIssuedIdUri == null) {
            (activity as? PartnerAuth)?.showError("Please select Government ID Proof.")
            return false
        }

        if (businessRegiDocUri == null) {
            (activity as? PartnerAuth)?.showError("Please select Business Registration Document.")
            return false
        }

        if (!isEmailVerified) {
            (activity as? PartnerAuth)?.showError("Please verify your Email.")
            return false
        }

        if (!isConditionAccepted) {
            (activity as? PartnerAuth)?.showError("Please accept terms and conditions.")
            return false
        }

        return true
    }

    private fun checkEmailValidation(): Boolean {
        val email = binding.emailAddressPartnerRegi.text.toString()
        val password = binding.passwordPartnerRegi.text.toString()
        val cPass = binding.confirmPassPartnerRegi.text.toString()

        if (email.isEmpty() || password.isEmpty() || cPass.isEmpty()) {
            (activity as? PartnerAuth)?.showError("Please fill all details...")
            return false
        }
        if (!email.endsWith("@gmail.com")) {
            (activity as? PartnerAuth)?.showError("Email address should ends with `@gmail.com` ")
            return false
        }
        if (password != cPass) {
            (activity as? PartnerAuth)?.showError("Password not matched")
            binding.passwordPartnerRegi.setText("")
            binding.confirmPassPartnerRegi.setText("")
            return false
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestPermissions(checkOne: Boolean) {
        val permissions = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        Dexter.withContext(requireContext()).withPermissions(permissions)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report?.areAllPermissionsGranted() == true) {
                        if (checkOne) {
                            pickGovernmentIDFromGallery()
                        } else {
                            pickRegistrationDocFromGallery()
                        }
                    }

                    if (report?.isAnyPermissionPermanentlyDenied == true) {
                        (context as? PartnerAuth)?.showError("Please grant all given permissions from app info!")
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?, p1: PermissionToken?
                ) {
                    p1?.continuePermissionRequest()
                }
            }).onSameThread().check()
    }

    private fun pickGovernmentIDFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher1.launch(intent)
    }

    private fun pickRegistrationDocFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher2.launch(intent)
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

    private fun verifyEmail() {
        (activity as? PartnerAuth)?.showDialog("Sending OTP...")
        val user = User(
            binding.emailAddressPartnerRegi.text.toString(),
            binding.passwordPartnerRegi.text.toString(),
            "partner",
            false,
            isAddressAdded = false
        )
        val apiService = ApiClient.getApiService()
        if (apiService != null) {
            apiService.registerUser(user)!!.enqueue(object : Callback<RegisterResponse?> {
                override fun onResponse(
                    call: Call<RegisterResponse?>, response: Response<RegisterResponse?>
                ) {
                    if (response.isSuccessful) {
                        (activity as? PartnerAuth)?.dismissDialog()
                        binding.otpWindowPartnerRegiLy.visibility = View.VISIBLE
                        (activity as? PartnerAuth)?.showError("OTP has been sent on ${binding.emailAddressPartnerRegi.text.toString()}")
                    } else {
                        Log.d(
                            "REGISTER_ERROR",
                            "Code: ${response.code()}, Body: ${response.errorBody()?.string()}"
                        )
                        (activity as? PartnerAuth)?.dismissDialog()
                    }
                }

                override fun onFailure(call: Call<RegisterResponse?>, t: Throwable) {
                    Log.e("REGISTER_ERROR", "Error: ${t.message}", t)
                    (activity as? PartnerAuth)?.dismissDialog()
                }
            })
        }
    }

    private fun verifyOtp(otp: String) {
        (activity as? PartnerAuth)?.showDialog("Verifying...")
        val email = binding.emailAddressPartnerRegi.text.toString()
        val apiService = ApiClient.getApiService()
        val user = UserOtpModel(
            email, otp
        )
        if (apiService != null) {
            apiService.verifyEmail(user)!!.enqueue(object : Callback<VerifyEmailResponseModel?> {
                override fun onResponse(
                    call: Call<VerifyEmailResponseModel?>,
                    response: Response<VerifyEmailResponseModel?>
                ) {
                    if (response.isSuccessful) {
                        binding.otpWindowPartnerRegiLy.visibility = View.GONE
                        binding.loginWindowLyPartnerRegi.visibility = View.GONE
                        binding.emailVerifiedTextPartnerRegi.visibility = View.VISIBLE
                        UserSharedPref(requireContext()).saveLoginStatus(
                            "partner", response.body()!!.token, false, isAddressAdded = false
                        )
                        isEmailVerified = true
                        (activity as? PartnerAuth)?.dismissDialog()
                        (activity as? PartnerAuth)?.showError("Email Verified Successfully!")
                    } else {
                        Log.e(
                            "LOGIN_ERROR",
                            "Code: ${response.code()}, Body: ${response.errorBody()?.string()}"
                        )
                        (activity as? PartnerAuth)?.dismissDialog()
                    }
                }

                override fun onFailure(call: Call<VerifyEmailResponseModel?>, t: Throwable) {
                    Log.e("LOGIN_ERROR", "Error: ${t.message}", t)
                    (activity as? PartnerAuth)?.dismissDialog()
                }
            })
        }
    }

    private fun updateDocStatus() {
        val email = binding.emailAddressPartnerRegi.text.toString()
        val docUploaded = true;

        val updateDocStatusModel = UpdateDocStatusModel(email, docUploaded)
        val apiService = ApiClient.getApiService()

        if (apiService != null) {
            apiService.updateDocStatus(updateDocStatusModel)
                ?.enqueue(object : Callback<RegisterResponse?> {
                    override fun onResponse(
                        call: Call<RegisterResponse?>, response: Response<RegisterResponse?>
                    ) {
                        if (response.isSuccessful) {
                            UserSharedPref(requireContext()).updateDocStatus(true)
                            (activity as? PartnerAuth)?.dismissDialog()
                            (activity as? PartnerAuth)?.showError("Document Status Updated!")
                            (activity as? PartnerAuth)?.startActivity(Intent(requireContext(),PartnerDashboard::class.java))
                            (activity as? PartnerAuth)?.finish()
                        } else {
                            (activity as? PartnerAuth)?.dismissDialog()
                            (activity as? PartnerAuth)?.showError(response.errorBody().toString())
                            Log.d("PARTNER_REGI_ERROR",response.errorBody().toString())
                        }
                    }

                    override fun onFailure(call: Call<RegisterResponse?>, t: Throwable) {
                        (activity as? PartnerAuth)?.showError(t.toString())
                        (activity as? PartnerAuth)?.dismissDialog()
                        Log.d("PARTNER_REGI_ERROR",t.toString())
                    }
                })
        }
    }

    private fun uploadPartnerDetails() {
        (activity as? PartnerAuth)?.showDialog("Processing...")
        val fullNameInput = binding.fullNamePartnerRegi.text.toString()
        val phoneNumberInput = binding.phoneNumberPartnerRegi.text.toString()

        val businessNameInput = binding.businessNamePartnerRegi.text.toString()
        val businessRegistrationNumberInput =
            binding.businessRegistrationNumberPartnerRegi.text.toString()
        val gstinInput = binding.gstinNumberPartnerRegi.text.toString()
        val fullAddressInput = binding.businessAddressPartnerRegi.text.toString()

        val bankAccHolderNameInput = binding.bankAccHolderNamePartnerRegi.text.toString()
        val backAccNumberInput = binding.bankAccNumberPartnerRegi.text.toString()
        val bankNameInput = binding.bankNamePartnerRegi.text.toString()
        val ifscInput = binding.ifscCodePartnerRegi.text.toString()

        val apiService = ApiClient.getApiService()

        CoroutineScope(Dispatchers.IO).launch {
            val fileFromUri = getFileFromContentUri(requireContext(), governmentIssuedIdUri!!)
            val issuedIdPart = fileFromUri?.let { (file, mimeType) ->
                val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
                MultipartBody.Part.createFormData("issuedIdPicture", file.name, requestFile)
            }

            // Get file and MIME type for businessRegiDocUri
            val fileFromUri2 = getFileFromContentUri(requireContext(), businessRegiDocUri!!)
            val businessRegiDocPart = fileFromUri2?.let { (file, mimeType) ->
                val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
                MultipartBody.Part.createFormData("businessRegiDocPicture", file.name, requestFile)
            }

            val fullName = RequestBody.create("text/plain".toMediaTypeOrNull(), fullNameInput)
            val phoneNumber =
                RequestBody.create("text/plain".toMediaTypeOrNull(), phoneNumberInput)
            val businessName =
                RequestBody.create("text/plain".toMediaTypeOrNull(), businessNameInput)
            val businessRegiNum =
                RequestBody.create(
                    "text/plain".toMediaTypeOrNull(),
                    businessRegistrationNumberInput
                )
            val gstin = RequestBody.create("text/plain".toMediaTypeOrNull(), gstinInput)
            val businessAddress =
                RequestBody.create("text/plain".toMediaTypeOrNull(), fullAddressInput)
            val bankAccHolderName =
                RequestBody.create("text/plain".toMediaTypeOrNull(), bankAccHolderNameInput)
            val bankAccNumber =
                RequestBody.create("text/plain".toMediaTypeOrNull(), backAccNumberInput)
            val bankName = RequestBody.create("text/plain".toMediaTypeOrNull(), bankNameInput)
            val ifsc = RequestBody.create("text/plain".toMediaTypeOrNull(), ifscInput)

            val call = apiService?.uploadPartnerDetails(
                fullName,
                phoneNumber,
                businessName,
                businessRegiNum,
                gstin,
                businessAddress,
                bankAccHolderName,
                bankAccNumber,
                bankName,
                ifsc,
                issuedIdPart!!,
                businessRegiDocPart!!
            )


            if (apiService != null) {
                call?.enqueue(object : Callback<RegisterResponse?> {
                    override fun onResponse(
                        call: Call<RegisterResponse?>, response: Response<RegisterResponse?>
                    ) {
                        if (response.isSuccessful) {
//                            (activity as? PartnerAuth)?.dismissDialog()
                            (activity as? PartnerAuth)?.showError("Account created and verified successfully!")
                            updateDocStatus()
                        } else {
                            (activity as? PartnerAuth)?.dismissDialog()
                            Log.d(
                                "PARTNER_REGI_ERROR",
                                response.errorBody().toString() + "\n" + response.message()
                            )
                        }
                    }

                    override fun onFailure(call: Call<RegisterResponse?>, t: Throwable) {
                        (activity as? PartnerAuth)?.dismissDialog()
                        Log.d("PARTNER_REGI_ERROR", t.message.toString())
                    }

                });
            }
        }
    }

    private fun getFileFromContentUri(context: Context, uri: Uri): Pair<File, String>? {
        try {
            // Extract file name and MIME type
            val fileName = getFileName(context, uri) ?: return null
            val mimeType = context.contentResolver.getType(uri) ?: "application/octet-stream"

            // Create temporary file with the original file name
            val tempFile = File(context.cacheDir, fileName)

            // Copy the content to the temporary file
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                tempFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            return Pair(tempFile, mimeType) // Return the file and mimeType
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    @SuppressLint("Range")
    private fun getFileName(context: Context, uri: Uri): String? {
        var fileName: String? = null
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                fileName = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            }
        }
        return fileName
    }


}