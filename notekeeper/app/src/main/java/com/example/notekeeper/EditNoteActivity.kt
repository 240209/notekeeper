package com.example.notekeeper

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.notekeeper.api.ApiClient
import com.example.notekeeper.databinding.ActivityEditNoteBinding
import com.example.notekeeper.models.NoteResponse
import com.example.notekeeper.utils.TokenManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class EditNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditNoteBinding

    // Holds the ID of the note being edited
    private var noteId: Int = -1

    // Calendar instance for date/time picking
    private val calendar = Calendar.getInstance()

    // Holds the due date of the note
    private var dueDate: Date = Date(0)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup action bar with back button and title
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = "NoteKeeper: Edit Note"
        }

        // Get note ID passed from intent
        noteId = intent.getIntExtra("note_id", -1)
        if (noteId == -1) {
            Toast.makeText(this, "Invalid Note ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Load existing note details from server
        loadNoteDetails(noteId)

        // Setup category spinner
        val categoryAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.categories,
            android.R.layout.simple_spinner_item
        )
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.editNoteCategory.adapter = categoryAdapter

        // Setup priority spinner
        val priorityAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.priorities,
            android.R.layout.simple_spinner_item
        )
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.editNotePriority.adapter = priorityAdapter

        // Set click listener for due date button
        binding.editNoteDueDate.setOnClickListener {
            showDateTimePicker()
        }

        // Save button click listener
        binding.saveButton.setOnClickListener {
            saveNote()
        }

        // Delete button click listener
        binding.deleteButton.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }

    // Opens Date and Time pickers to select due date
    private fun showDateTimePicker() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)

            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)

                // Update due date and display it
                dueDate = calendar.time
                val displayDate = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).apply {
                    timeZone = TimeZone.getDefault()
                }.format(dueDate)
                binding.editNoteDueDate.setText("Due: $displayDate")
            }

            // Show time picker after date selection
            TimePickerDialog(
                this,
                timeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        // Show date picker
        DatePickerDialog(
            this,
            dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    // Loads note details from the backend and populates fields
    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadNoteDetails(noteId: Int) {
        val token = TokenManager.getToken(this)
        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getNoteById("Token $token", noteId)
                if (response.isSuccessful) {
                    val note = response.body()
                    if (note != null) {
                        // Populate fields with note data
                        binding.editNoteTitle.setText(note.title)
                        binding.editNoteBody.setText(note.body)
                        binding.editNotePriority.setSelection(note.priority - 1)
                        binding.editNoteCategory.setSelection(
                            resources.getStringArray(R.array.categories).indexOf(note.category)
                        )

                        // Format and display due date
                        note.due_date?.let {
                            dueDate = UtcStringToUtcDate(it)
                            val formattedDate = UtcDateToLocalString(dueDate)
                            binding.editNoteDueDate.setText("Due: $formattedDate")
                            calendar.time = dueDate
                        }

                        // Display created and modified timestamps
                        note.created_at?.let {
                            val createdAtString = UtcDateToLocalString(UtcStringToUtcDate(it))
                            binding.editNoteCreatedAt.text = "Note created at: $createdAtString"
                        }
                        note.modified_at?.let {
                            val modifiedAtString = UtcDateToLocalString(UtcStringToUtcDate(it))
                            binding.editNoteModifiedAt.text = "Last edit at: $modifiedAtString"
                        }
                    } else {
                        Toast.makeText(this@EditNoteActivity, "Note not found", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } else {
                    Toast.makeText(this@EditNoteActivity, "Failed to load note", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@EditNoteActivity, "Error loading note", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Shows a confirmation dialog before deleting the note
    private fun showDeleteConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirm Deletion")
        builder.setMessage("Are you sure you want to delete the note?")
        builder.setPositiveButton("Yes") { _, _ -> deleteNote() }
        builder.setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }

    // Sends a request to delete the note from the backend
    private fun deleteNote() {
        val token = TokenManager.getToken(this)
        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.deleteNote("Token $token", noteId)
                if (response.isSuccessful) {
                    Toast.makeText(this@EditNoteActivity, "Note deleted", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@EditNoteActivity, "Failed to delete note", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@EditNoteActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Sends updated note data to the backend
    private fun saveNote() {
        val title = binding.editNoteTitle.text.toString().trim()
        val body = binding.editNoteBody.text.toString().trim()
        val category = binding.editNoteCategory.selectedItem.toString()
        val priority = binding.editNotePriority.selectedItem.toString().toIntOrNull() ?: 0

        val dueDateString = UtcDateToUtcString(dueDate)

        if (title.isEmpty() || body.isEmpty()) {
            Toast.makeText(this, "Title and body are required", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedNote = NoteResponse(
            id = noteId,
            title = title,
            body = body,
            category = category,
            priority = priority,
            due_date = dueDateString,
            created_at = null,
            modified_at = null
        )

        val token = TokenManager.getToken(this)
        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.updateNote("Token $token", noteId, updatedNote)
                if (response.isSuccessful) {
                    Toast.makeText(this@EditNoteActivity, "Note updated", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@EditNoteActivity, "Failed to update note", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@EditNoteActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Converts a UTC ISO 8601 string to a Date object
    @RequiresApi(Build.VERSION_CODES.O)
    fun UtcStringToUtcDate(UtcString: String?): Date {
        if (UtcString.isNullOrEmpty()) return Date(0)

        return try {
            val instant = java.time.Instant.parse(UtcString)
            Date.from(instant)
        } catch (e: Exception) {
            val correctedUtcString = UtcString.split('.')[0]
            val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            isoFormat.timeZone = TimeZone.getTimeZone("UTC")
            isoFormat.parse(correctedUtcString) ?: Date(0)
        }
    }

    // Converts a UTC Date to local time formatted string
    fun UtcDateToLocalString(UtcDate: Date?): String {
        if (UtcDate == null) return ""

        val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        format.timeZone = TimeZone.getDefault()
        return format.format(UtcDate)
    }

    // Converts a UTC Date to string formatted in UTC
    fun UtcDateToUtcString(UtcDate: Date?): String {
        if (UtcDate == null) return ""

        val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        format.timeZone = TimeZone.getTimeZone("UTC")
        return format.format(UtcDate)
    }

    // Handles action bar back button press
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}
