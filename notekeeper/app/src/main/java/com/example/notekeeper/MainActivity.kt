package com.example.notekeeper

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.notekeeper.api.ApiClient
import com.example.notekeeper.utils.TokenManager

// MainActivity serves as the entry point of the app.
// It checks if the user is logged in and redirects to either NotesActivity or LoginActivity.
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the API client with application context
        ApiClient.init(this)

        // Set the layout for the activity
        setContentView(R.layout.activity_main)

        // Redirect the user based on login state
        redirect()
    }

    // Checks if a valid user token is stored
    private fun checkUserLoggedIn(): Boolean {
        // Retrieve token from SharedPreferences (or other secure storage)
        val token = TokenManager.getToken(this)
        // Return true if token exists and is not empty
        return !token.isNullOrEmpty()
    }

    // Redirects the user to the appropriate screen based on authentication status
    private fun redirect() {
        // Determine login status
        val isLoggedIn = checkUserLoggedIn()

        if (isLoggedIn) {
            // If logged in, navigate to NotesActivity
            startActivity(Intent(this, NotesActivity::class.java))
        } else {
            // If not logged in, navigate to LoginActivity
            startActivity(Intent(this, LoginActivity::class.java))
        }

        // Finish MainActivity so it’s not kept in the back stack
        finish()
    }

    // Ensures redirection logic runs again when returning to this activity (e.g. after back press)
    override fun onResume() {
        super.onResume()
        redirect()
    }
}
