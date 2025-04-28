package com.example.notekeeper.utils

import android.content.Context
import androidx.core.content.edit

object TokenManager {
    fun saveToken(context: Context, token: String) {
        val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        prefs.edit() { putString("auth_token", token) }
    }

    fun getToken(context: Context): String? {
        val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        return prefs.getString("auth_token", null)
    }

    fun clearToken(context: Context) {
        val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        prefs.edit() { putString("auth_token", "") }
    }
}
