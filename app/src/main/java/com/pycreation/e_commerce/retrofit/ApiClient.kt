package com.pycreation.e_commerce.retrofit

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.pycreation.e_commerce.APIs
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class ApiClient {

    private var retrofit: Retrofit? = null

    private val gson: Gson = GsonBuilder()
        .setLenient()
        .create()

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS) // Connection timeout
        .writeTimeout(60, TimeUnit.SECONDS)   // Write timeout
        .readTimeout(60, TimeUnit.SECONDS)    // Read timeout
        .addInterceptor(logging)
        .build()


    fun getClient(): Retrofit? {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(APIs.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build()
        }
        return retrofit
    }

    companion object {
        private var instance: ApiClient? = null

        private fun getInstance(): ApiClient {
            if (instance == null) {
                instance = ApiClient()
            }
            return instance!!
        }

        fun getApiService(): ApiService? {
            return getInstance().getClient()?.create(ApiService::class.java)
        }
    }
}