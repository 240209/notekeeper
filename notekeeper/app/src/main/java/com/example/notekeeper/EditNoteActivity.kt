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
    private var noteId: Int = -1
    private val calendar = Calendar.getInstance()
    private var dueDate: Date = Date(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = "NoteKeeper: Edit Note"
        }

        noteId = intent.getIntExtra("note_id", -1)
        if (noteId == -1) {
            Toast.makeText(this, "Invalid Note ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadNoteDetails(noteId)

        val categoryAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.categories,
            android.R.layout.simple_spinner_item
        )
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.editNoteCategory.adapter = categoryAdapter

        val priorityAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.priorities,
            android.R.layout.simple_spinner_item
        )
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.editNotePriority.adapter = priorityAdapter

        binding.editNoteDueDate.setOnClickListener {
            showDateTimePicker()
        }

        binding.saveButton.setOnClickListener {
            saveNote()
        }

        binding.deleteButton.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }

    private fun showDateTimePicker() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)

            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)

                dueDate = calendar.time // Keep as-is (local time for now)
                val displayDate = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).apply {
                    timeZone = TimeZone.getDefault()
                }.format(dueDate)
                binding.editNoteDueDate.setText("Due: $displayDate")
            }

            TimePickerDialog(
                this,
                timeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        DatePickerDialog(
            this,
            dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

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
                        binding.editNoteTitle.setText(note.title)
                        binding.editNoteBody.setText(note.body)
                        binding.editNotePriority.setSelection(note.priority - 1)
                        binding.editNoteCategory.setSelection(
                            resources.getStringArray(R.array.categories).indexOf(note.category)
                        )

                        note.due_date?.let {
                            dueDate = UtcStringToUtcDate(it)

                            val formattedDate = UtcDateToLocalString(dueDate)
                            binding.editNoteDueDate.setText("Due: $formattedDate")

                            calendar.time = dueDate
                        }

                        note.created_at?.let {
                            var createdAtString = UtcDateToLocalString(UtcStringToUtcDate(note.created_at))
                            binding.editNoteCreatedAt.text = "Note created at: ${createdAtString}"
                        }
                        note.modified_at?.let {
                            var modifiedAtString = UtcDateToLocalString(UtcStringToUtcDate(note.modified_at))
                            binding.editNoteModifiedAt.text = "Last edit at: ${modifiedAtString}"
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

    private fun showDeleteConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirm Deletion")
        builder.setMessage("Are you sure you want to delete the note?")
        builder.setPositiveButton("Yes") { _, _ ->
            deleteNote()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

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

    @RequiresApi(Build.VERSION_CODES.O)
    fun UtcStringToUtcDate(UtcString: String?): Date {
        if (UtcString.isNullOrEmpty()) return Date(0)

        // Use Instant for proper ISO 8601 parsing if available (API 26+)
        return try {
            val instant = java.time.Instant.parse(UtcString)
            Date.from(instant)
        } catch (e: Exception) {
            // Fallback for legacy (incomplete ISO parser)
            val correctedUtcString = UtcString.split('.')[0]
            val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            isoFormat.timeZone = TimeZone.getTimeZone("UTC")
            isoFormat.parse(correctedUtcString) ?: Date(0)
        }
    }


    fun UtcDateToLocalString(UtcDate: Date?): String {
        if (UtcDate == null) return ""

        val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        format.timeZone = TimeZone.getDefault()
        return format.format(UtcDate)
    }


    fun UtcDateToUtcString(UtcDate: Date?): String {
        if (UtcDate == null) return ""

        val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        format.timeZone = TimeZone.getTimeZone("UTC")
        return format.format(UtcDate)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}