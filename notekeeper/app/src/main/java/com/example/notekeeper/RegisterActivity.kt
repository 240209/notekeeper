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

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)  // Show the back arrow
            setDisplayShowHomeEnabled(true)  // Show the icon on the left
            title = "NoteKeeper: Register"  // Optional: Change the title of the action bar
        }

        binding.registerButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val passwordCheck = binding.passwordCheckEditText.text.toString()

            if (password != passwordCheck)
                Toast.makeText(this@RegisterActivity, "Passwords do not match.", Toast.LENGTH_SHORT).show()
            else {
                lifecycleScope.launch {
                    try {
                        val response = ApiClient.apiService.registerUser(
                            RegisterRequest(
                                username,
                                email,
                                password
                            )
                        )
                        if (response.isSuccessful) {
                            val registeredUsername = response.body()?.username
                            Toast.makeText(
                                this@RegisterActivity,
                                "User $registeredUsername created successfully!",
                                Toast.LENGTH_LONG
                            ).show()
                            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(
                                this@RegisterActivity,
                                "Error: ${response.message()}",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Error: ${e.message}",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
            }
        }

    }

    // Handle the back button press (when the up button in the action bar is pressed)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            // When the back arrow is pressed, go back to the previous activity
            onBackPressedDispatcher.onBackPressed()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}
