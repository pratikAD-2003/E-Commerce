package com.pycreation.e_commerce.consumer

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.pycreation.e_commerce.Constants
import com.pycreation.e_commerce.MyWidgets
import com.pycreation.e_commerce.admin.models.PartnerModel
import com.pycreation.e_commerce.consumer.models.User
import com.pycreation.e_commerce.consumer.models.UserFirebaseModel

class UserFirebase : MyWidgets() {
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference

    fun signupUser(userFirebaseModel: UserFirebaseModel) {
        db.collection(Constants.USER_AUTH).document(userFirebaseModel.token).set(userFirebaseModel)
            .addOnSuccessListener {
                dismissDialog()
            }.addOnFailureListener {
                dismissDialog()
            }
    }

    fun registerPartnerUser(
        partnerModel: PartnerModel,
        token: String,
        callback: (Boolean?) -> Unit
    ) {
        saveImageToFirebaseStorage(
            partnerModel.issuedIdPicture,
            Constants.GOVERNMENT_ID_PROOF
        ) { uri1 ->
            if (uri1 != null) {
                saveImageToFirebaseStorage(
                    partnerModel.businessRegiDocPicture,
                    Constants.BUSINESS_REGISTRATION
                ) { uri2 ->
                    if (uri2 != null) {
                        partnerModel.issuedIdPicture = uri1
                        partnerModel.businessRegiDocPicture = uri2

                        db.collection(Constants.PARTNER_DETAILS).document(token).set(partnerModel)
                            .addOnSuccessListener {
                                callback(true)
                            }.addOnFailureListener {
                                callback(false)
                            }
                    } else {
                        Log.e("Firebase", "Business registration image upload failed")
                        callback(false)
                    }
                }
            } else {
                Log.e("Firebase", "Government ID image upload failed")
                callback(false)
            }
        }
    }

    private fun saveImageToFirebaseStorage(uri: String, path: String, callback: (String?) -> Unit) {
        val fileRef = storageRef.child(path)
            .child("image" + System.currentTimeMillis() + ".png")
        fileRef.putFile(Uri.parse(uri)).addOnSuccessListener {
            fileRef.downloadUrl.addOnSuccessListener { downloadableUri ->
                callback(downloadableUri.toString())
            }.addOnFailureListener {
                callback(null)
            }
        }.addOnFailureListener {
            callback(null)
        }
    }
}