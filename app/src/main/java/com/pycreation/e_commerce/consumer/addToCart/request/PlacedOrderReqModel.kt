package com.pycreation.e_commerce.consumer.addToCart.request

data class PlacedOrderReqModel(
    val deliveryAddress: String,
    val paymentMethod: String,
    val userId: String
)