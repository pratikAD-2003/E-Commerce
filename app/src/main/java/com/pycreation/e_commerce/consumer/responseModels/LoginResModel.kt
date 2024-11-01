package com.pycreation.e_commerce.consumer.responseModels

import java.io.Serializable

data class LoginResModel(
    val message : String,
    val token : String,
    val type : String,
    val docUploaded : Boolean,
    val isAddressAdded : Boolean
) : Serializable
