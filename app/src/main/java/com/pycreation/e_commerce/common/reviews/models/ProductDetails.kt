package com.pycreation.e_commerce.common.reviews.models

data class ProductDetails(
    val availableStock: Int,
    val brand: String,
    val businessName: String,
    val cashOnDelivery: Boolean,
    val deliveryCharges: Int,
    val freeDelivery: Boolean,
    val productCategory: String,
    val productDescription: String,
    val productImages: List<String>,
    val productName: String,
    val productPrice: Int,
    val productSellingPrice: Int,
    val productUid: String,
    val ratings: Double,
    val returnPolicy: String,
    val reviewsCount: Int,
    val sellerUid: String,
    val subCategory: String,
    val tags: List<String>,
    val timestamp: String,
    val warranty: String
)