package com.pycreation.e_commerce.common.reviews.models

data class PostReviewReqModel(
    val productId: String,
    val rating: String,
    val reviewText: String,
    val reviewerId: String,
    val reviewerName: String
)