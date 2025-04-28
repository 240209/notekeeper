package com.example.notekeeper

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.notekeeper.api.ApiClient
import com.example.notekeeper.utils.TokenManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        ApiClient.init(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        redirect()
    }

    private fun checkUserLoggedIn(): Boolean {
        // Check if the user token exists in SharedPreferences (or any other method)
        return TokenManager.getToken(this)?.isNotEmpty() ?: false
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
    }

    override fun onResume() {
        super.onResume()
        redirect()
    }
}
