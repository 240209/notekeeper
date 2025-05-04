package com.example.notekeeper.api

import android.content.Context
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Singleton object that manages the creation and access to the Retrofit API client
object ApiClient {
    // Base URL for the API endpoints
    private const val BASE_URL = "http://10.0.1.51:8000/"

    // Lazy-initialized Retrofit instance configured with base URL and Gson converter
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL) // Sets the base URL for network requests
            .addConverterFactory(GsonConverterFactory.create()) // Adds support for JSON conversion using Gson
            .build() // Builds the Retrofit instance
    }

    // Lazily creates an implementation of the API interface (ApiService)
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java) // Creates an implementation of the defined API endpoints
    }

    // Optional initialization function (currently unused)
    fun init(context: Context) {}
}
