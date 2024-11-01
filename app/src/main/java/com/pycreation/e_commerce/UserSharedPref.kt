package com.pycreation.e_commerce

import android.content.Context
import android.content.Context.MODE_PRIVATE

open class UserSharedPref(context: Context) {
    private val sp = context.getSharedPreferences(Constants.USER_SAVED, MODE_PRIVATE)
    private val editor = sp.edit()

    fun saveLoginStatus(
        type: String,
        token: String,
        docUploaded: Boolean,
        isAddressAdded: Boolean
    ) {
        editor.putString(Constants.userType, type)
        editor.putString(Constants.TOKEN, token)
        editor.putBoolean(Constants.DOC_UPLOADED, docUploaded)
        editor.putBoolean(Constants.ADDRESS_ADDED, isAddressAdded)
        editor.apply()
    }

    fun getUserType(): String {
        return sp.getString(Constants.userType, "undefined")!!
    }

    fun updateDocStatus(boolean: Boolean) {
        editor.putBoolean(Constants.DOC_UPLOADED, boolean)
        editor.apply()
    }

    fun getDocStatus(): Boolean {
        return sp.getBoolean(Constants.DOC_UPLOADED, false)
    }

    fun getAddressStatus(): Boolean {
        return sp.getBoolean(Constants.ADDRESS_ADDED, false)
    }

    fun getToken(): String {
        return sp.getString(Constants.TOKEN, "undefined")!!
    }

    fun deleteExistence() {
        editor.putString(Constants.userType, null)
        editor.putString(Constants.TOKEN, null)
        editor.putBoolean(Constants.DOC_UPLOADED, false)
        editor.putBoolean(Constants.ADDRESS_ADDED, false)
        editor.apply()
    }
}