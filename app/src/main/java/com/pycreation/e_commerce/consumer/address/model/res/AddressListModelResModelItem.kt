package com.pycreation.e_commerce.consumer.address.model.res

data class AddressListModelResModelItem(
    val _id: String,
    val area: String,
    val city: String,
    val fullName: String,
    val phoneNumber: Long,
    val pinCode: Int,
    val state: String,
    val type: String,
    val userId: String
)