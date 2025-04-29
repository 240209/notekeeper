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

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "NoteKeeper: Login"

        binding.loginButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.loginButton.isEnabled = false // optional UX improvement

            lifecycleScope.launch {
                try {
                    val response = ApiClient.apiService.loginUser(LoginRequest(username, password))
                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        loginResponse?.token?.let { token ->
                            TokenManager.saveToken(this@LoginActivity, token)
                            startActivity(Intent(this@LoginActivity, NotesActivity::class.java))
                            finish()
                        } ?: run {
                            Toast.makeText(this@LoginActivity, "Unexpected error: Empty token", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@LoginActivity, "Login failed: Invalid credentials", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@LoginActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                } finally {
                    binding.loginButton.isEnabled = true
                }
            }
        }

        binding.registerButton.setOnClickListener {
            try {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
