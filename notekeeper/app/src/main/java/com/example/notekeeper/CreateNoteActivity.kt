package com.example.notekeeper

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.notekeeper.api.ApiClient
import com.example.notekeeper.databinding.ActivityCreateNoteBinding
import com.example.notekeeper.models.NoteRequest
import com.example.notekeeper.utils.TokenManager
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.util.*

class CreateNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateNoteBinding
    private lateinit var authToken: String
    private lateinit var dueDateButton: Button
    private var dueDate: String = "2000-01-01 00:00"

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = "NoteKeeper: New Note"
        }

        // Category spinner
        val categoryAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.categories,
            android.R.layout.simple_spinner_item
        )
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.categorySpinner.adapter = categoryAdapter

        // Priority spinner
        val priorityAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.priorities,
            android.R.layout.simple_spinner_item
        )
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.prioritySpinner.adapter = priorityAdapter

        // Due date selection
        dueDateButton = findViewById(R.id.dueDateButton)
        dueDateButton.setOnClickListener { showDatePickerDialog() }
        dueDateButton.text = "Due: $dueDate"

        authToken = TokenManager.getToken(this) ?: ""

        binding.createNoteButton.setOnClickListener {
            val title = binding.titleEditText.text.toString().trim()
            val body = binding.bodyEditText.text.toString().trim()
            val category = binding.categorySpinner.selectedItem.toString()
            val priority = binding.prioritySpinner.selectedItem.toString().toIntOrNull() ?: 0

            if (title.isEmpty() || body.isEmpty()) {
                Toast.makeText(this, "Please fill out both title and body", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            createNote(title, body, category, priority, dueDate)
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(this, { _, year, month, day ->
            showTimePickerDialog(year, month, day)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        datePickerDialog.show()
    }

    @SuppressLint("DefaultLocale", "SetTextI18n")
    private fun showTimePickerDialog(year: Int, month: Int, day: Int) {
        val calendar = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(this, { _, hour, minute ->
            dueDate = String.format("%04d-%02d-%02d %02d:%02d", year, month + 1, day, hour, minute)
            dueDateButton.text = "Due: $dueDate"
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)

        timePickerDialog.show()
    }

    private fun createNote(title: String, body: String, category: String, priority: Int, dueDate: String) {
        val noteRequest = NoteRequest(
            title = title,
            body = body,
            category = category,
            priority = priority,
            due_date = if (dueDate.isEmpty()) "" else dueDate
        )

        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.createNote("Token $authToken", noteRequest)
                if (response.isSuccessful) {
                    Toast.makeText(this@CreateNoteActivity, "Note created successfully!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@CreateNoteActivity, "Error: ${response.code()} ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: HttpException) {
                Toast.makeText(this@CreateNoteActivity, "HTTP Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                Toast.makeText(this@CreateNoteActivity, "Network Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
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
