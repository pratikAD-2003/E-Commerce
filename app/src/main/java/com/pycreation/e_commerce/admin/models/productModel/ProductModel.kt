package com.pycreation.e_commerce.admin.models.productModel

import java.io.Serializable

data class ProductModel(
    val message : String,
    val productList: List<Product>
) : Serializable