package com.pycreation.e_commerce.consumer.models

import java.io.Serializable

data class UserFirebaseModel(val email : String,val token : String,val type : String) : Serializable
