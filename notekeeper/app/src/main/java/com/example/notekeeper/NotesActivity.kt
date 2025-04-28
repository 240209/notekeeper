package com.example.notekeeper

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notekeeper.api.ApiClient
import com.example.notekeeper.databinding.ActivityNotesBinding
import com.example.notekeeper.utils.TokenManager
import kotlinx.coroutines.launch

class NotesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = "NoteKeeper: My notes"  // Optional: Change the title of the action bar
        }

        binding.notesRecyclerView.layoutManager = LinearLayoutManager(this)

        binding.addNoteButton.setOnClickListener {
            startActivity(Intent(this, CreateNoteActivity::class.java))
        }
    }

    fun loadNotes() {
        val token = TokenManager.getToken(this)
        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getNotes("Token $token")
                if (response.isSuccessful) {
                    val notes = response.body() ?: emptyList()
                    binding.notesRecyclerView.adapter = NotesAdapter(notes)
                } else {
                    Toast.makeText(this@NotesActivity, "Failed to load notes", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@NotesActivity, "Error loading notes", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadNotes()
    }

    // Inflate the menu with the logout button
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_activity_notes, menu) // Inflate the menu
        return true
    }

    // Handle the menu item clicks
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout_button -> {
                // Handle the logout logic here
                logoutUser()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Logout logic to handle user logout
    private fun logoutUser() {
        val token = TokenManager.getToken(this)

        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.logoutUser("Token $token")
                if (response.isSuccessful) {
                    TokenManager.clearToken(this@NotesActivity)
                    startActivity(Intent(this@NotesActivity, LoginActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@NotesActivity, "Error logging out.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@NotesActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }


    }
}
