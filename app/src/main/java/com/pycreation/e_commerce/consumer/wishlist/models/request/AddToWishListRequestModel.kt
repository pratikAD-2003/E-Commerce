package com.pycreation.e_commerce.consumer.wishlist.models.request

data class AddToWishListRequestModel(
    val productUid: String,
    val userId: String
)