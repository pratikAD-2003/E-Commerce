package com.pycreation.e_commerce.consumer.orders.res

import com.pycreation.e_commerce.admin.models.productModel.Product

data class Item(
    val _id: String,
    val productDetails: Product,
    val productUid: String,
    val quantity: Int
)