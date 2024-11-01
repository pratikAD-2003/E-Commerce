package com.pycreation.e_commerce.consumer.models

import java.io.Serializable

data class UserOtpModel(val email: String, val otp: String) : Serializable