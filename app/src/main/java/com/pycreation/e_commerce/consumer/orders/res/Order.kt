package com.pycreation.e_commerce.consumer.orders.res

data class Order(
    val deliveryAddress: String,
    val items: List<Item>,
    val orderId: String,
    val orderShippingCost: Int,
    val orderStatus: String,
    val orderTotalPrice: Int,
    val paymentMethod: String,
    val paymentStatus: String,
    val timestamp: String,
    val totalPrice: Double,
    val userId: String
)