package com.example.notekeeper

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.notekeeper.api.ApiClient
import com.example.notekeeper.databinding.ActivityRegisterBinding
import com.example.notekeeper.models.RegisterRequest
import kotlinx.coroutines.launch

// RegisterActivity handles the user registration process where the user provides their details
// to create a new account. It sends the registration request to the server and processes the response.
class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    // onCreate is called when the activity is first created.
    // It sets up the UI, including initializing the binding and setting the action bar title.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater) // Inflate the view using ViewBinding
        setContentView(binding.root) // Set the content view to the root of the layout

        // Configure the action bar to allow going back to the previous activity
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true) // Show the back button in the action bar
            setDisplayShowHomeEnabled(true) // Show the home button in the action bar
            title = "NoteKeeper: Register" // Set the title of the action bar
        }

        // Set a click listener on the register button to handle user input and initiate registration
        binding.registerButton.setOnClickListener {
            // Retrieve input from the text fields
            val username = binding.usernameEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString()
            val passwordCheck = binding.passwordCheckEditText.text.toString()

            // Validate that no fields are empty
            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || passwordCheck.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show() // Show error message
                return@setOnClickListener
            }

            // Validate that the passwords match
            if (password != passwordCheck) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show() // Show error message
                return@setOnClickListener
            }

            // Disable the register button to prevent multiple submissions
            binding.registerButton.isEnabled = false

            // Perform the registration operation in a coroutine
            lifecycleScope.launch {
                try {
                    // Send the registration request to the API
                    val response = ApiClient.apiService.registerUser(
                        RegisterRequest(username, email, password) // Send user data as a RegisterRequest object
                    )
                    // Check if the registration was successful
                    if (response.isSuccessful) {
                        // Extract the registered username or use the input one if it's not in the response
                        val registeredUsername = response.body()?.username ?: username
                        Toast.makeText(
                            this@RegisterActivity,
                            "User $registeredUsername created successfully!", // Success message
                            Toast.LENGTH_LONG
                        ).show()
                        // Redirect the user to the LoginActivity after successful registration
                        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                        finish() // Finish the current activity
                    } else {
                        // If registration fails, show the failure message from the response
                        Toast.makeText(
                            this@RegisterActivity,
                            "Registration failed: ${response.message()}", // Error message from the response
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    // Catch any exceptions and show an error message
                    Toast.makeText(
                        this@RegisterActivity,
                        "Error: ${e.localizedMessage}", // Show the localized error message
                        Toast.LENGTH_SHORT
                    ).show()
                } finally {
                    // Re-enable the register button after the operation is complete
                    binding.registerButton.isEnabled = true
                }
            }
        }
    }

    // onOptionsItemSelected is called when an item in the options menu is selected.
    // It handles the back navigation when the user taps the home/back button in the action bar.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            // When the back button is pressed, perform the back navigation
            onBackPressedDispatcher.onBackPressed()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}
