package com.pycreation.e_commerce.common.reviews.models

data class ReviewModelItem(
    val productId: String,
    val rating: Int,
    val reviewText: String,
    val reviewerId: String,
    val reviewerName: String,
    val timestamp: String
)