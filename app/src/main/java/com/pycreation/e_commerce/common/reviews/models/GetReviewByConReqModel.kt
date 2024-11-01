package com.pycreation.e_commerce.common.reviews.models

data class GetReviewByConReqModel(
    val productId: String,
    val reviewerId: String
)