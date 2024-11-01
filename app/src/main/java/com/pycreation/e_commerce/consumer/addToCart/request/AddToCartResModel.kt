package com.pycreation.e_commerce.consumer.addToCart.request

data class AddToCartResModel(
    val productUid: String,
    val quantity: Int,
    val userId: String
)