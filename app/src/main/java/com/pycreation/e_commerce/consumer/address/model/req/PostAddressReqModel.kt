package com.pycreation.e_commerce.consumer.address.model.req

data class PostAddressReqModel(
    val area: String,
    val city: String,
    val fullName: String,
    val phoneNumber: String,
    val pinCode: String,
    val state: String,
    val type: String,
    val userId: String
)