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

        binding.loginButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            supportActionBar?.apply {
                title = "NoteKeeper: Login"  // Optional: Change the title of the action bar
            }

            lifecycleScope.launch {
                try {
                    val response = ApiClient.apiService.loginUser(LoginRequest(username, password))
                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        loginResponse?.token?.let { token ->
                            TokenManager.saveToken(this@LoginActivity, token)
                            startActivity(Intent(this@LoginActivity, NotesActivity::class.java))
                            finish()
                        }
                    } else {
                        Toast.makeText(this@LoginActivity, "Login failed", Toast.LENGTH_SHORT)
                            .show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        binding.registerButton.setOnClickListener {
            try {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}
