package com.pycreation.e_commerce.consumer.models

import java.io.Serializable

data class User(val email: String, val password: String, val type: String?=null,val docUploaded : Boolean = false,val isAddressAdded : Boolean = false) : Serializable