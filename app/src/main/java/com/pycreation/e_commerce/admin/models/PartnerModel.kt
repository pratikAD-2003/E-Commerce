package com.pycreation.e_commerce.admin.models

import java.io.Serializable

data class PartnerModel(
    val fullName: String = "",
    val phoneNumber: String = "",
    val businessName: String = "",
    val businessRegistrationNumber: String = "",
    val gstin: String = "",
    val fullBusinessAddress: String = "",
    val bankAccHolderName: String = "",
    val bankAccNumber: String = "",
    val bankName: String = "",
    val ifsc: String = "",
    var issuedIdPicture: String = "",
    var businessRegiDocPicture: String = ""
) : Serializable
