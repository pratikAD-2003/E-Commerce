package com.pycreation.e_commerce.consumer.orders.req

data class OrderByOrderIdReqModel(
    val orderId: String,
    val userId: String
)