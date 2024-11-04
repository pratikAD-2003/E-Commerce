package com.pycreation.e_commerce

import android.app.Dialog
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.pycreation.e_commerce.databinding.ActivityMyWidgetsBinding

open class MyWidgets : AppCompatActivity() {
    private var doubleBackToExitPressedOnce = false
    private var mProgressDialog: Dialog? = null
    private lateinit var binding: ActivityMyWidgetsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMyWidgetsBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        mProgressDialog = Dialog(this)
    }


    public fun showDialog(text: String) {
        mProgressDialog = Dialog(this@MyWidgets)
        mProgressDialog?.setCancelable(false)
        val view = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null)
        mProgressDialog?.setContentView(view)
        mProgressDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val pText = view.findViewById<TextView>(R.id.status_text)
        pText.text = text
        mProgressDialog?.show()
    }

    public fun dismissDialog() {
        mProgressDialog?.dismiss()
    }

    fun doubleBackToExit() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this@MyWidgets, "Please tap again to exit", Toast.LENGTH_SHORT).show()

        Handler().postDelayed({
            doubleBackToExitPressedOnce = false
        }, 2000)
    }

    fun showError(message: String) {
        val snackBar =
            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(ContextCompat.getColor(this, R.color.outlinedColor))
        snackBar.show()
    }

    open fun navigateTo(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.userAuthFrameLayout, fragment)
            .addToBackStack(null)  // Add to backstack so user can navigate back
            .commit()
    }

    open fun navigateToWithoutStackTrace(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.userAuthFrameLayout, fragment)
            .commit()
    }

    open fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val networkCapabilities = it.getNetworkCapabilities(it.activeNetwork)
                return networkCapabilities != null
            } else {
                val networkInfo = it.activeNetworkInfo
                return networkInfo != null && networkInfo.isConnected
            }
        }
        return false
    }


}