package com.pycreation.e_commerce.common.models

import java.io.Serializable

data class ForgetPassBodyModel(
    val email : String,
    val newPassword : String
) : Serializable
