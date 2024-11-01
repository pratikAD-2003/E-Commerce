package com.pycreation.e_commerce.consumer.addToCart.response.cartresmodel

import com.pycreation.e_commerce.admin.models.productModel.Product

data class Cart(
    val _id: String,
    val productDetails: Product,
    val productUid: String,
    val quantity: Int
)