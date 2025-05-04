package com.example.notekeeper

import androidx.appcompat.app.AlertDialog
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

// NotesActivity is responsible for displaying the user's notes and allowing them to add new notes or log out.
class NotesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotesBinding

    // onCreate is called when the activity is created.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout and bind it to the activity
        binding = ActivityNotesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set the title for the action bar (Optional)
        supportActionBar?.apply {
            title = "NoteKeeper: My notes"
        }

        // Set up the RecyclerView to display notes in a linear layout
        binding.notesRecyclerView.layoutManager = LinearLayoutManager(this)

        // Set the click listener for the 'Add Note' button to navigate to CreateNoteActivity
        binding.addNoteButton.setOnClickListener {
            startActivity(Intent(this, CreateNoteActivity::class.java))
        }
    }

    // loadNotes fetches the notes from the backend and displays them in the RecyclerView
    fun loadNotes() {
        val token = TokenManager.getToken(this)

        // Use lifecycleScope to perform the network operation asynchronously
        lifecycleScope.launch {
            try {
                // If no token exists, notify the user and redirect to login
                if (token.isNullOrEmpty()) {
                    Toast.makeText(
                        this@NotesActivity,
                        "Session expired, please log in again.",
                        Toast.LENGTH_SHORT
                    ).show()
                    startActivity(Intent(this@NotesActivity, LoginActivity::class.java))
                    finish()
                    return@launch
                }

                // Make an API request to fetch the user's notes
                val response = ApiClient.apiService.getNotes("Token $token")

                // If the response is successful, display the notes in the RecyclerView
                if (response.isSuccessful) {
                    val notes = response.body() ?: emptyList()
                    binding.notesRecyclerView.adapter = NotesAdapter(notes)
                } else {
                    // If the API request failed, show a toast message
                    Toast.makeText(this@NotesActivity, "Failed to load notes", Toast.LENGTH_SHORT)
                        .show()
                }
            } catch (e: Exception) {
                // If an exception occurs, display an error toast
                Toast.makeText(this@NotesActivity, "Error loading notes", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // onResume is called when the activity comes into the foreground, and reloads the notes
    override fun onResume() {
        super.onResume()
        loadNotes()
    }

    // onCreateOptionsMenu inflates the menu for the activity, including the logout button
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_activity_notes, menu) // Inflate the menu resource file
        return true
    }

    // onOptionsItemSelected handles the selection of menu items, such as logging out
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout_button -> {
                // If the logout button is clicked, show a confirmation dialog
                showLogoutConfirmationDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // showLogoutConfirmationDialog shows a dialog asking the user to confirm their logout action
    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirm Logout")
        builder.setMessage("Are you sure you want to logout?")

        // If the user confirms logout, perform the logout action
        builder.setPositiveButton("Yes") { _, _ ->
            logoutUser()
        }

        // If the user cancels, dismiss the dialog
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        // Show the dialog
        builder.create().show()
    }

    // logoutUser handles the process of logging the user out, including removing the token and redirecting to LoginActivity
    private fun logoutUser() {
        val token = TokenManager.getToken(this)

        // Perform the logout operation asynchronously
        lifecycleScope.launch {
            try {
                // Make an API request to log out the user
                val response = ApiClient.apiService.logoutUser("Token $token")

                // If the logout is successful, clear the stored token and redirect to the login screen
                if (response.isSuccessful) {
                    TokenManager.clearToken(this@NotesActivity)
                    startActivity(Intent(this@NotesActivity, LoginActivity::class.java))
                    finish()
                } else {
                    // If there was an error during logout, show a toast message
                    Toast.makeText(this@NotesActivity, "Error logging out.", Toast.LENGTH_SHORT)
                        .show()
                }
            } catch (e: Exception) {
                // If an exception occurs during logout, show an error message
                Toast.makeText(this@NotesActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
