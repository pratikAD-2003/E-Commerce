package com.pycreation.e_commerce.consumer.wishlist.models.response

import com.pycreation.e_commerce.admin.models.productModel.Product

data class Wishlist(
    val itemId: String,
    val productDetails: Product,
    val productUid: String,
    val userId: String
)