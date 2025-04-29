package com.example.notekeeper

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.notekeeper.api.ApiClient
import com.example.notekeeper.utils.TokenManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ApiClient.init(this)
        setContentView(R.layout.activity_main)

        redirect()
    }

    private fun checkUserLoggedIn(): Boolean {
        // Check if the user token exists in SharedPreferences (or any other method)
        val token = TokenManager.getToken(this)
        return !token.isNullOrEmpty()
    }

    private fun redirect() {
        // Check if user is logged in (this can be improved with shared preferences or token storage)
        val isLoggedIn = checkUserLoggedIn()

        if (isLoggedIn) {
            // Proceed to Notes Activity
            startActivity(Intent(this, NotesActivity::class.java))
        } else {
            // Redirect to login
            startActivity(Intent(this, LoginActivity::class.java))
        }
        finish()
    }

    override fun onResume() {
        super.onResume()
        redirect()
    }
}
