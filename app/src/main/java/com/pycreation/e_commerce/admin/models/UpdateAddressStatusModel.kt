package com.pycreation.e_commerce.admin.models

import java.io.Serializable

data class UpdateAddressStatusModel(
    val email: String,
    val isAddressAdded: Boolean
) : Serializable
