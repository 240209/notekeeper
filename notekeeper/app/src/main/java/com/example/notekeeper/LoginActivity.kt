package com.example.notekeeper

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.notekeeper.api.ApiClient
import com.example.notekeeper.databinding.ActivityLoginBinding
import com.example.notekeeper.models.LoginRequest
import com.example.notekeeper.utils.TokenManager
import kotlinx.coroutines.launch

// LoginActivity handles user login, token storage, and navigation to main notes screen.
class LoginActivity : AppCompatActivity() {

    // ViewBinding for activity_login.xml layout
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set the ActionBar title
        supportActionBar?.title = "NoteKeeper: Login"

        // Set click listener for the login button
        binding.loginButton.setOnClickListener {
            // Get and trim input fields
            val username = binding.usernameEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            // Validate input fields
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Optionally disable login button to prevent multiple clicks
            binding.loginButton.isEnabled = false

            // Launch coroutine to handle login API request
            lifecycleScope.launch {
                try {
                    // Create and send login request
                    val response = ApiClient.apiService.loginUser(LoginRequest(username, password))
                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        // Save token and proceed if response is valid
                        loginResponse?.token?.let { token ->
                            TokenManager.saveToken(this@LoginActivity, token)
                            startActivity(Intent(this@LoginActivity, NotesActivity::class.java))
                            finish()
                        } ?: run {
                            // Handle unexpected null token
                            Toast.makeText(this@LoginActivity, "Unexpected error: Empty token", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // Handle login failure (e.g. invalid credentials)
                        Toast.makeText(this@LoginActivity, "Login failed: Invalid credentials", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    // Catch network or unexpected errors
                    Toast.makeText(this@LoginActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                } finally {
                    // Re-enable the login button
                    binding.loginButton.isEnabled = true
                }
            }
        }

        // Set click listener for register button to navigate to RegisterActivity
        binding.registerButton.setOnClickListener {
            try {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            } catch (e: Exception) {
                // Catch and show any error during navigation
                Toast.makeText(this@LoginActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
