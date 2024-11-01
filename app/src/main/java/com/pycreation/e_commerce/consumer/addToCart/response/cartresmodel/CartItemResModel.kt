package com.pycreation.e_commerce.consumer.addToCart.response.cartresmodel

data class CartItemResModel(
    val cart: List<Cart>,
    val totalPrice: Int,
    val totalShippingCost: Int,
    val platform : Float
)