package com.pycreation.e_commerce.admin.models.productSpecifications

data class ProductSpecificationsItem(
    val productUid: String,
    val specifications: List<Specification>
)