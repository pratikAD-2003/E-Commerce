package com.pycreation.e_commerce.consumer.responseModels

import java.io.Serializable

data class VerifyEmailResponseModel(
    val message : String,
    val token : String
) : Serializable
