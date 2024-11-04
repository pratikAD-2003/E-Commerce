package com.pycreation.e_commerce.admin.listening

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.pycreation.e_commerce.Constants
import com.pycreation.e_commerce.R
import com.pycreation.e_commerce.UserSharedPref
import com.pycreation.e_commerce.admin.PartnerAuth
import com.pycreation.e_commerce.admin.listening.adapter.ImageAdapter
import com.pycreation.e_commerce.admin.listening.adapter.TagAdapter
import com.pycreation.e_commerce.admin.models.productSpecifications.ProductSpecificationsItem
import com.pycreation.e_commerce.admin.models.productSpecifications.Specification
import com.pycreation.e_commerce.consumer.responseModels.RegisterResponse
import com.pycreation.e_commerce.databinding.FragmentPartnerProductPostBinding
import com.pycreation.e_commerce.retrofit.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class PartnerProductPostFrag : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentPartnerProductPostBinding
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>

    private val selectedImageUris = mutableListOf<Uri>()
    private lateinit var imageAdapter: ImageAdapter

    private val selectedTags = mutableListOf<String>()
    private lateinit var tagAdapter: TagAdapter

    private var specsList: List<String>? = null
    private var isCodAvailable: String = "no selected"
    private var isFreeDeliveryAvailable: String = "no selected"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPartnerProductPostBinding.inflate(inflater, container, false)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finish()
        }
        setupActionBar()
        setCategoryMenu()
        setReturnPolicyMenu()
        setWarrantyMenu()
        setupCodMenu()
        setupFreeDeliveryMenu()
        setDeliveryChargesMenu()

        imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val data: Intent? = result.data
                    data?.let {
                        if (it.clipData != null) {
                            val count = it.clipData!!.itemCount
                            for (i in 0 until count) {
                                val imageUri = it.clipData!!.getItemAt(i).uri
                                if (selectedImageUris.size < 5 && !selectedImageUris.contains(
                                        imageUri
                                    )
                                ) {
                                    selectedImageUris.add(imageUri)
                                }
                            }
                        } else if (it.data != null) {
                            val imageUri = it.data!!
                            if (selectedImageUris.size < 5 && !selectedImageUris.contains(imageUri)) {
                                selectedImageUris.add(imageUri)
                            }
                        }
                        imageAdapter.notifyDataSetChanged()
                    }
                }
            }

        imageAdapter = ImageAdapter(selectedImageUris, { uri ->
            selectedImageUris.remove(uri)
            imageAdapter.notifyDataSetChanged()
        }) {
            if (selectedImageUris.size > 5) {
                (activity as PartnerProLisActivity?)?.showError("Maximum 5 Pictures allowed!")
            } else {
                requestPermissions()
            }
        }

        binding.productListImageRecyclerview.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.productListImageRecyclerview.adapter = imageAdapter

        binding.postProductPartnerProPost.setOnClickListener {
            if (checkValidation()) {
                postProductToServer()
            }
        }

        tagAdapter = TagAdapter(selectedTags) { string ->
            selectedTags.remove(string)
            tagAdapter.notifyDataSetChanged()
        }

        binding.tagRecyclerviewPartnerProPost.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.tagRecyclerviewPartnerProPost.adapter = tagAdapter

        binding.addTagBtnPartnerProPost.setOnClickListener {
            val tag = binding.productListTagsInputPartnerProPost.text.toString()
            if (tag.isNotEmpty() && selectedTags.size < 5) {
                selectedTags.add(tag)
                binding.productListTagsInputPartnerProPost.text?.clear()
                tagAdapter.notifyDataSetChanged()
            } else {
                (activity as? PartnerProLisActivity)?.showError("Maximum 5 tags allowed!")
                binding.productListTagsInputPartnerProPost.text?.clear()
            }
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PartnerProductPostFrag().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun postProductToServer() {
        (activity as? PartnerProLisActivity)?.showDialog("Processing...")
        val category = binding.categoryAutocompleteText.text.toString()
        val subCategory = binding.subCategoryAutocompleteText.text.toString()
        val productName = binding.productNamePartnerProPost.text.toString()
        val productDes = binding.productDesPartnerProPost.text.toString()
        val brand = binding.brandProPostPost.text.toString()
        val orgPrice = binding.originalPricePartProPost.text.toString()
        val sellingPrice = binding.sellingPricePartnerProductPost.text.toString()
        val availableStock = binding.availableStockPartnerProPost.text.toString()
        val businessName = binding.businessNamePartnerProductPost.text.toString()
        val returnPolicy = binding.returnPolicyAutoComplete.text.toString()

        val apiService = ApiClient.getApiService()

        CoroutineScope(Dispatchers.IO).launch {
            val productImages: MutableList<MultipartBody.Part> = mutableListOf()

            for (imageUri in selectedImageUris) {
                val fileFromUri = getFileFromContentUri(requireContext(), imageUri)
                val issuedIdPart = fileFromUri?.let { (file, mimeType) ->
                    val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("productImages", file.name, requestFile)
                }
                productImages.add(issuedIdPart!!)
            }

            val productName =
                RequestBody.create("text/plain".toMediaTypeOrNull(), productName)
            val productDescription =
                RequestBody.create("text/plain".toMediaTypeOrNull(), productDes)
            val productPrice = RequestBody.create("text/plain".toMediaTypeOrNull(), orgPrice)
            val productSellingPrice =
                RequestBody.create("text/plain".toMediaTypeOrNull(), sellingPrice)
            val brand = RequestBody.create("text/plain".toMediaTypeOrNull(), brand)
            val returnPolicy =
                RequestBody.create("text/plain".toMediaTypeOrNull(), returnPolicy)
            val productCategory =
                RequestBody.create("text/plain".toMediaTypeOrNull(), category)
            val subCategory = RequestBody.create("text/plain".toMediaTypeOrNull(), subCategory)
            val availableStock =
                RequestBody.create("text/plain".toMediaTypeOrNull(), availableStock)
            val businessName =
                RequestBody.create("text/plain".toMediaTypeOrNull(), businessName)
            val sellerUid = RequestBody.create(
                "text/plain".toMediaTypeOrNull(),
                UserSharedPref(requireContext()).getToken()
            )
            val tags =
                RequestBody.create("text/plain".toMediaTypeOrNull(), selectedTags.joinToString(","))
            val warrantyInput =
                RequestBody.create(
                    "text/plain".toMediaTypeOrNull(),
                    binding.warrantyAutoTextPartProductPost.text.toString()
                )

            val cashOnDelivery =
                RequestBody.create(
                    "text/plain".toMediaTypeOrNull(),
                    if (isCodAvailable == "Yes") "true" else "false"
                )

            val freeDelivery =
                RequestBody.create(
                    "text/plain".toMediaTypeOrNull(),
                    if (isFreeDeliveryAvailable == "Yes") "true" else "false"
                )

            val deliveryCharges = RequestBody.create(
                "text/plain".toMediaTypeOrNull(),
                if (isFreeDeliveryAvailable == "No") binding.deliveryChargesAutoTextPartProductPost.text.toString() else "0"
            )

            val call = apiService?.postProduct(
                productName,
                productDescription,
                productPrice,
                productSellingPrice,
                brand,
                returnPolicy,
                productCategory,
                subCategory,
                availableStock,
                businessName,
                sellerUid,
                warrantyInput,
                tags,
                cashOnDelivery,
                freeDelivery,
                deliveryCharges,
                productImages
            )
            call?.enqueue(object : Callback<RegisterResponse?> {
                override fun onResponse(
                    call: Call<RegisterResponse?>,
                    response: Response<RegisterResponse?>
                ) {
                    if (response.isSuccessful) {
                        Log.d("ERROR", response.body()!!.message)
                        postSpecifications(response.body()!!.message)
                    } else {
                        (activity as? PartnerProLisActivity)?.dismissDialog()
                        Log.d("ERROR", response.errorBody().toString())
                    }
                }

                override fun onFailure(call: Call<RegisterResponse?>, t: Throwable) {
                    Log.d("ERROR_2", t.message.toString())
                    (activity as? PartnerProLisActivity)?.dismissDialog()
                    t.message?.let { (activity as? PartnerProLisActivity)?.showError(it) }
                }
            })
        }
    }

    private fun postSpecifications(productUid: String) {
        val specificationsList = listOf<Specification>(
            Specification(specsList!![0], binding.productSpec1.text.toString()),
            Specification(specsList!![1], binding.productSpec2.text.toString()),
            Specification(specsList!![2], binding.productSpec3.text.toString()),
            Specification(specsList!![3], binding.productSpec4.text.toString()),
            Specification(specsList!![4], binding.productSpec5.text.toString()),
            Specification(specsList!![5], binding.productSpec6.text.toString())
        )
        val productSpecs = ProductSpecificationsItem(productUid, specificationsList)

        val apiService = ApiClient.getApiService()
        if (apiService != null) {
            apiService.postProductSpecsByUid(productSpecs)
                ?.enqueue(object : Callback<RegisterResponse?> {
                    override fun onResponse(
                        call: Call<RegisterResponse?>,
                        response: Response<RegisterResponse?>
                    ) {
                        if (response.isSuccessful) {
                            Log.d("SPECS_STATUS", response.body()!!.message)
                            (activity as? PartnerProLisActivity)?.dismissDialog()
                            (activity as? PartnerProLisActivity)?.showError(response.body()!!.message)
                            (activity as AppCompatActivity).finish()
                        } else {
                            Log.d("SPECS_STATUS", response.errorBody().toString())
                            (activity as? PartnerProLisActivity)?.dismissDialog()
                        }
                    }

                    override fun onFailure(call: Call<RegisterResponse?>, t: Throwable) {
                        Log.d("SPECS_STATUS", t.message.toString())
                        (activity as? PartnerProLisActivity)?.dismissDialog()
                    }

                })
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

    private fun checkValidation(): Boolean {
        val category = binding.categoryAutocompleteText.text.toString()
        val subCategory = binding.subCategoryAutocompleteText.text.toString()
        val productName = binding.productNamePartnerProPost.text.toString()
        val productDes = binding.productDesPartnerProPost.text.toString()
        val brand = binding.brandProPostPost.text.toString()
        val orgPrice = binding.originalPricePartProPost.text.toString()
        val sellingPrice = binding.sellingPricePartnerProductPost.text.toString()
        val availableStock = binding.availableStockPartnerProPost.text.toString()
        val businessName = binding.businessNamePartnerProductPost.text.toString()
        val returnPolicy = binding.returnPolicyAutoComplete.text.toString()
        val warrantyPeriod = binding.warrantyAutoTextPartProductPost.text.toString()
        val isConditionAccepted = binding.acceptConditionsPartnerProPost.isChecked

        if (selectedImageUris.size < 2) {
            (activity as PartnerProLisActivity?)?.showError("Minimum 2 Product Pictures needed.")
            return false
        }

        if (category.isEmpty()) {
            (activity as PartnerProLisActivity?)?.showError("Please select Product Category.")
            return false
        }
        if (subCategory.isEmpty()) {
            (activity as PartnerProLisActivity?)?.showError("Please select Product SubCategory.")
            return false
        }
        if (productName.isEmpty()) {
            (activity as PartnerProLisActivity?)?.showError("Please enter Product Name.")
            return false
        }

        if (productDes.isEmpty()) {
            (activity as PartnerProLisActivity?)?.showError("Please enter Product Description.")
            return false
        }

        if (brand.isEmpty()) {
            (activity as PartnerProLisActivity?)?.showError("Please enter Product Brand.")
            return false
        }

        if (orgPrice.isEmpty()) {
            (activity as PartnerProLisActivity?)?.showError("Please enter Product Original Price.")
            return false
        }

        if (sellingPrice.isEmpty()) {
            (activity as PartnerProLisActivity?)?.showError("Please enter Product Selling Price.")
            return false
        }

        if (availableStock.isEmpty()) {
            (activity as PartnerProLisActivity?)?.showError("Please enter Product In Stock.")
            return false
        }

        if (businessName.isEmpty()) {
            (activity as PartnerProLisActivity?)?.showError("Please enter Business Name.")
            return false
        }


        if (returnPolicy.isEmpty()) {
            (activity as PartnerProLisActivity?)?.showError("Please select Return Policy.")
            return false
        }

        if (warrantyPeriod.isEmpty()) {
            (activity as PartnerProLisActivity?)?.showError("Please select Warranty Period.")
            return false
        }

        if (isCodAvailable == "no selected") {
            (activity as PartnerProLisActivity?)?.showError("Please select Cash On Delivery Option.")
            return false
        }

        if (isFreeDeliveryAvailable == "no selected") {
            (activity as PartnerProLisActivity?)?.showError("Please select Free Delivery Option.")
            return false
        }

        if (isFreeDeliveryAvailable == "No" && binding.deliveryChargesAutoTextPartProductPost.text.toString()
                .isEmpty()
        ) {
            (activity as PartnerProLisActivity?)?.showError("Please select Delivery Charges Option.")
            return false
        }

        if (selectedTags.isEmpty()) {
            (activity as PartnerProLisActivity?)?.showError("Please enter at least 1 Tag.")
            return false
        }

        if (binding.productSpec1.text.toString().isEmpty() || binding.productSpec2.text.toString()
                .isEmpty() || binding.productSpec3.text.toString()
                .isEmpty() || binding.productSpec4.text.toString()
                .isEmpty() || binding.productSpec4.text.toString()
                .isEmpty() || binding.productSpec6.text.toString().isEmpty()
        ) {
            (activity as PartnerProLisActivity?)?.showError("Please enter Specifications details.")
            return false
        }
        if (!isConditionAccepted) {
            (activity as PartnerProLisActivity?)?.showError("Please accept Terms & Conditions.")
            return false
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestPermissions() {
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
                        val intent =
                            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        imagePickerLauncher.launch(intent)
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

    private fun setupCodMenu() {
        val adapter = ArrayAdapter<String>(
            requireContext(),
            R.layout.drop_down_item_custom_ly,
            Constants.codList
        )

        binding.codAutoTextPartProductPost.setOnItemClickListener { parent, view, position, id ->
            val selected = parent.getItemAtPosition(position).toString()
            if (selected == "Yes") {
                isCodAvailable = "Yes"
            } else if (selected == "No") {
                isCodAvailable = "No"
            }
        }
        binding.codAutoTextPartProductPost.setAdapter(adapter)
    }

    private fun setupFreeDeliveryMenu() {
        val adapter = ArrayAdapter<String>(
            requireContext(),
            R.layout.drop_down_item_custom_ly,
            Constants.codList
        )

        binding.freeDeliveryAutoTextPartProductPost.setOnItemClickListener { parent, view, position, id ->
            val selected = parent.getItemAtPosition(position).toString()
            if (selected == "Yes") {
                binding.deliveryChargesLayoutProDe.visibility = View.GONE
                isFreeDeliveryAvailable = "Yes"
            } else if (selected == "No") {
                binding.deliveryChargesLayoutProDe.visibility = View.VISIBLE
                isFreeDeliveryAvailable = "No"
            }
        }
        binding.freeDeliveryAutoTextPartProductPost.setAdapter(adapter)
    }

    private fun setDeliveryChargesMenu() {
        val adapter = ArrayAdapter<String>(
            requireContext(),
            R.layout.drop_down_item_custom_ly,
            Constants.deliveryCharges
        )
        binding.deliveryChargesAutoTextPartProductPost.setAdapter(adapter)
    }

    private fun setupActionBar() {
        (activity as AppCompatActivity).setSupportActionBar(binding.productListeningToolbar)
        val actionBar = (activity as AppCompatActivity).supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
        }
        binding.productListeningToolbar.setNavigationOnClickListener { (activity as AppCompatActivity).finish() }
    }

    private fun setCategoryMenu() {
        val adapter = ArrayAdapter<String>(
            requireContext(),
            R.layout.drop_down_item_custom_ly,
            Constants.productCategories
        )
        binding.categoryAutocompleteText.setAdapter(adapter)

        binding.categoryAutocompleteText.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position).toString()
            binding.subCategoryLayoutPartnerProPost.visibility = View.VISIBLE
            binding.subCategoryAutocompleteText.setText("")
            binding.subCategoryAutocompleteText.hint = "Select Product Subcategory"
            binding.productSpecText.visibility = View.VISIBLE
            showProductSpecs(selectedItem)
            val subCategoryList = getSubCategory(selectedItem)
            setSubCategory(subCategoryList)
        }
    }

    private fun setWarrantyMenu() {
        val adapter = ArrayAdapter<String>(
            requireContext(),
            R.layout.drop_down_item_custom_ly,
            Constants.warrantyList
        )
        binding.warrantyAutoTextPartProductPost.setAdapter(adapter)
    }

    private fun setReturnPolicyMenu() {
        val adapter = ArrayAdapter<String>(
            requireContext(),
            R.layout.drop_down_item_custom_ly,
            Constants.returnPolicyList
        )
        binding.returnPolicyAutoComplete.setAdapter(adapter)

        binding.returnPolicyAutoComplete.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position).toString()
        }
    }

    private fun setSubCategory(list: List<String>) {
        val adapter = ArrayAdapter<String>(
            requireContext(),
            R.layout.drop_down_item_custom_ly,
            list
        )
        binding.subCategoryAutocompleteText.setAdapter(adapter)

        binding.subCategoryAutocompleteText.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position).toString()
        }
    }

    private fun getSubCategory(category: String): List<String> {
        when (category) {
            "Electronics" -> {
                return Constants.electronicsCat
            }

            "Fashion" -> {
                return Constants.fashionCat
            }

            "Home & Furniture" -> {
                return Constants.homeAndFurnitureCat
            }

            "Beauty & Personal Care" -> {
                return Constants.beautyAndPersonalCareCat
            }

            "Books & Stationery" -> {
                return Constants.booksAndStationeryCat
            }

            "Sports,Fitness & Outdoors" -> {
                return Constants.sportFitnessOutdoorsCat
            }

            "Toys,Kids & Baby Products" -> {
                return Constants.toysKidsBabyProCat
            }

            "Groceries & Essentials" -> {
                return Constants.groceriesAndEssentialsCat
            }

            "Automotive" -> {
                return Constants.automotiveCat
            }

            "Health & Nutrition" -> {
                return Constants.healthAndNutritionCat
            }

            "Gaming" -> {
                return Constants.gamingCat
            }

            "Travel & Luggage" -> {
                return Constants.travelAndLuggageCat
            }

            "Pet Supplies" -> {
                return Constants.petSuppliesCat
            }

            "Jewelry" -> {
                return Constants.jewelryCat
            }

            else -> return listOf()
        }
    }

    private fun showProductSpecs(category: String) {
        when (category) {
            "Electronics" -> {
                specsList =
                    listOf("Battery Life", "Screen Size", "Weight", "Processor", "RAM", "Storage")
                binding.productSpec1.hint = "Battery Life"
                binding.productSpec2.hint = "Screen Size"
                binding.productSpec3.hint = "Weight"
                binding.productSpec4.hint = "Processor"
                binding.productSpec5.hint = "RAM"
                binding.productSpec6.hint = "Storage"
                binding.specificationLayoutPartnerProdPost.visibility = View.VISIBLE
            }

            "Fashion" -> {
                specsList =
                    listOf("Material", "Size", "Color", "Brand", "Care Instruction", "Fit Type")
                binding.productSpec1.hint = "Material"
                binding.productSpec2.hint = "Size"
                binding.productSpec3.hint = "Color"
                binding.productSpec4.hint = "Brand"
                binding.productSpec5.hint = "Care Instruction"
                binding.productSpec6.hint = "Fit Type"
                binding.specificationLayoutPartnerProdPost.visibility = View.VISIBLE
            }

            "Home & Furniture" -> {
                specsList = listOf(
                    "Material",
                    "Dimensions(Length * Width * Height)",
                    "Weight with Unit",
                    "Color",
                    "Assembly Required",
                    "Style"
                )
                binding.productSpec1.hint = "Material"
                binding.productSpec2.hint = "Dimensions(Length * Width * Height)"
                binding.productSpec3.hint = "Weight with Unit"
                binding.productSpec4.hint = "Color"
                binding.productSpec5.hint = "Assembly Required"
                binding.productSpec6.hint = "Style"
                binding.specificationLayoutPartnerProdPost.visibility = View.VISIBLE
            }

            "Beauty & Personal Care" -> {
                specsList = listOf(
                    "Volume",
                    "Skin Type",
                    "Fragrance",
                    "Cruelty-Free",
                    "Paraben-Free",
                    "Application Method"
                )
                binding.productSpec1.hint = "Volume"
                binding.productSpec2.hint = "Skin Type"
                binding.productSpec3.hint = "Fragrance"
                binding.productSpec4.hint = "Cruelty-Free"
                binding.productSpec5.hint = "Paraben-Free"
                binding.productSpec6.hint = "Application Method"
                binding.specificationLayoutPartnerProdPost.visibility = View.VISIBLE
            }

            "Books & Stationery" -> {
                specsList = listOf("Author", "Genre", "Pages", "ISBN", "Publisher", "Language")
                binding.productSpec1.hint = "Author"
                binding.productSpec2.hint = "Genre"
                binding.productSpec3.hint = "Pages"
                binding.productSpec4.hint = "ISBN"
                binding.productSpec5.hint = "Publisher"
                binding.productSpec6.hint = "Language"
                binding.specificationLayoutPartnerProdPost.visibility = View.VISIBLE
            }

            "Sports,Fitness & Outdoors" -> {
                specsList = listOf(
                    "Material",
                    "Weight",
                    "Dimensions(Length * Width * Height)",
                    "Capacity",
                    "Activity Type",
                    "Water Resistance"
                )
                binding.productSpec1.hint = "Material"
                binding.productSpec2.hint = "Weight"
                binding.productSpec3.hint = "Dimensions(Length * Width * Height)"
                binding.productSpec4.hint = "Capacity"
                binding.productSpec5.hint = "Activity Type"
                binding.productSpec6.hint = "Water Resistance"
                binding.specificationLayoutPartnerProdPost.visibility = View.VISIBLE
            }

            "Toys,Kids & Baby Products" -> {
                specsList = listOf(
                    "Age Group",
                    "Material",
                    "Battery Required",
                    "Dimensions(Length * Width * Height)",
                    "Safety Standard",
                    "Color"
                )
                binding.productSpec1.hint = "Age Group"
                binding.productSpec2.hint = "Material"
                binding.productSpec3.hint = "Battery Required"
                binding.productSpec4.hint = "Dimensions(Length * Width * Height)"
                binding.productSpec5.hint = "Safety Standard"
                binding.productSpec6.hint = "Color"
                binding.specificationLayoutPartnerProdPost.visibility = View.VISIBLE
            }

            "Groceries & Essentials" -> {
                specsList = listOf(
                    "Expiration Date",
                    "Weight",
                    "Ingredients",
                    "Organic",
                    "Storage Instruction",
                    "Serving Size"
                )
                binding.productSpec1.hint = "Expiration Date"
                binding.productSpec2.hint = "Weight"
                binding.productSpec3.hint = "Ingredients"
                binding.productSpec4.hint = "Organic"
                binding.productSpec5.hint = "Storage Instructions"
                binding.productSpec6.hint = "Serving Size"
                binding.specificationLayoutPartnerProdPost.visibility = View.VISIBLE
            }

            "Automotive" -> {
                specsList = listOf(
                    "Compatibility",
                    "Material",
                    "Weight",
                    "Dimensions",
                    "Installation Type",
                    "Warranty"
                )
                binding.productSpec1.hint = "Compatibility"
                binding.productSpec2.hint = "Material"
                binding.productSpec3.hint = "Weight"
                binding.productSpec4.hint = "Dimensions"
                binding.productSpec5.hint = "Installation Type"
                binding.productSpec6.hint = "Warranty"
                binding.specificationLayoutPartnerProdPost.visibility = View.VISIBLE
            }

            "Health & Nutrition" -> {
                specsList = listOf(
                    "Serving Size",
                    "Calories",
                    "Ingredients",
                    "Allergen Information",
                    "Expiry Date",
                    "Diet Type"
                )
                binding.productSpec1.hint = "Serving Size"
                binding.productSpec2.hint = "Calories"
                binding.productSpec3.hint = "Ingredients"
                binding.productSpec4.hint = "Allergen Information"
                binding.productSpec5.hint = "Expiry Date"
                binding.productSpec6.hint = "Diet Type"
                binding.specificationLayoutPartnerProdPost.visibility = View.VISIBLE
            }

            "Gaming" -> {
                specsList = listOf(
                    "Platform",
                    "Genre",
                    "Rating",
                    "Developer",
                    "Release Date",
                    "Multiplayer"
                )
                binding.productSpec1.hint = "Platform (You can select multiple)"
                binding.productSpec2.hint = "Genre"
                binding.productSpec3.hint = "Rating"
                binding.productSpec4.hint = "Developer"
                binding.productSpec5.hint = "Release Date"
                binding.productSpec6.hint = "Multiplayer"
                binding.specificationLayoutPartnerProdPost.visibility = View.VISIBLE
            }

            "Travel & Luggage" -> {
                specsList = listOf(
                    "Material",
                    "Dimensions(Length * Width * Height",
                    "Weight",
                    "Capacity",
                    "Water Resistance",
                    "Warranty"
                )
                binding.productSpec1.hint = "Material"
                binding.productSpec2.hint = "Dimensions(Length * Width * Height)"
                binding.productSpec3.hint = "Weight"
                binding.productSpec4.hint = "Capacity"
                binding.productSpec5.hint = "Water Resistance"
                binding.productSpec6.hint = "Warranty"
                binding.specificationLayoutPartnerProdPost.visibility = View.VISIBLE
            }

            "Pet Supplies" -> {
                specsList = listOf(
                    "Type",
                    "Weight",
                    "Flavor",
                    "Age Group",
                    "Special Diet",
                    "Nutritional Information"
                )
                binding.productSpec1.hint = "Type"
                binding.productSpec2.hint = "Weight"
                binding.productSpec3.hint = "Flavor"
                binding.productSpec4.hint = "Age Group"
                binding.productSpec5.hint = "Special Diet"
                binding.productSpec6.hint = "Nutritional Information"
                binding.specificationLayoutPartnerProdPost.visibility = View.VISIBLE
            }

            "Jewelry" -> {
                specsList = listOf(
                    "Material",
                    "Weight",
                    "Gemstone",
                    "Length",
                    "Care Instructions",
                    "Design"
                )
                binding.productSpec1.hint = "Material"
                binding.productSpec2.hint = "Weight"
                binding.productSpec3.hint = "Gemstone"
                binding.productSpec4.hint = "Length"
                binding.productSpec5.hint = "Care Instructions"
                binding.productSpec6.hint = "Design"
                binding.specificationLayoutPartnerProdPost.visibility = View.VISIBLE
            }

            "Watches & Accessories" -> {
                specsList = listOf(
                    "Brand",
                    "Material",
                    "Water Resistance",
                    "Dial Color",
                    "Case Diameter",
                    "Battery Type"
                )
                binding.productSpec1.hint = "Brand"
                binding.productSpec2.hint = "Material"
                binding.productSpec3.hint = "Water Resistance"
                binding.productSpec4.hint = "Dial Color"
                binding.productSpec5.hint = "Case Diameter"
                binding.productSpec6.hint = "Battery Type"
                binding.specificationLayoutPartnerProdPost.visibility = View.VISIBLE
            }
        }
    }
}