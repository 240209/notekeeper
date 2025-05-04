package com.example.notekeeper.utils

import android.content.Context
import androidx.core.content.edit

// Singleton object responsible for storing, retrieving, and clearing the user's authentication token
object TokenManager {
    // Key used to store the token in SharedPreferences
    private const val TOKEN_KEY = "user_token"

    // Name of the SharedPreferences file
    private const val PREF_NAME = "auth_prefs"

    // Saves the provided token into SharedPreferences
    fun saveToken(context: Context, token: String) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit() { putString(TOKEN_KEY, token) } // Store token with the key
    }

    // Retrieves the stored token from SharedPreferences
    fun getToken(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(TOKEN_KEY, null) // Return token if it exists, otherwise null
    }

    // Removes the stored token from SharedPreferences
    fun clearToken(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit() { remove(TOKEN_KEY) } // Delete the token entry
    }
}
