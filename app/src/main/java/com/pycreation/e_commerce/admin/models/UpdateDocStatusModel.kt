package com.pycreation.e_commerce.admin.models

import java.io.Serializable

data class UpdateDocStatusModel(
    val email: String,
    val docUploaded: Boolean
) : Serializable
