package com.example.notekeeper.utils

import android.content.Context
import androidx.core.content.edit

object TokenManager {
    private const val TOKEN_KEY = "user_token"
    private const val PREF_NAME = "auth_prefs"

    fun saveToken(context: Context, token: String) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit() { putString(TOKEN_KEY, token) }
    }

    fun getToken(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(TOKEN_KEY, null)
    }

    fun clearToken(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit() { remove(TOKEN_KEY) }
    }
}
