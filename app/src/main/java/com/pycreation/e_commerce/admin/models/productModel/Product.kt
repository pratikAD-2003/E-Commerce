package com.pycreation.e_commerce.admin.models.productModel

import java.io.Serializable

data class Product(
    val sellerUid: String,
    val productName: String,
    val productDescription: String,
    val productPrice: Double,
    val productSellingPrice: Double,
    val brand: String,
    val productCategory: String,
    val subCategory: String,
    val productImages: List<String>,
    val ratings: Double,
    val reviewsCount: Int,
    val returnPolicy: String,
    val availableStock: Int,
    val businessName: String,
    val productUid: String,
    val timestamp: String,
    val tags : List<String>,
    val cashOnDelivery : Boolean,
    val warranty : String,
    val freeDelivery : Boolean,
    val deliveryCharges : Int
) : Serializable