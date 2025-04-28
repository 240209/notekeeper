package com.example.notekeeper

import android.annotation.SuppressLint
import android.content.Intent
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
import java.text.SimpleDateFormat
import java.util.*
import android.app.DatePickerDialog
import android.app.TimePickerDialog

class CreateNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateNoteBinding
    private lateinit var authToken: String

    private lateinit var dueDateButton: Button
    private var dueDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)  // Show the back arrow
            setDisplayShowHomeEnabled(true)  // Show the icon on the left
            title = "NoteKeeper: New note"  // Optional: Change the title of the action bar
        }

        val categoryAdapter = ArrayAdapter.createFromResource(
            this@CreateNoteActivity,
            R.array.categories_array,  // Define this in res/values/strings.xml
            android.R.layout.simple_spinner_item
        )
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.categorySpinner.adapter = categoryAdapter

        val priorityAdapter = ArrayAdapter.createFromResource(
            this@CreateNoteActivity,
            R.array.priority_array,  // Define this in res/values/strings.xml
            android.R.layout.simple_spinner_item
        )
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.prioritySpinner.adapter = priorityAdapter

        dueDateButton = findViewById(R.id.dueDateButton)
        // Set an onClickListener on the due date button to show DatePickerDialog
        dueDateButton.setOnClickListener {
            showDatePickerDialog()
        }

        // Get auth token from Intent extras
        authToken = TokenManager.getToken(this@CreateNoteActivity) ?: ""

        binding.createNoteButton.setOnClickListener {
            val title = binding.titleEditText.text.toString()
            val body = binding.bodyEditText.text.toString()
            val category = binding.categorySpinner.selectedItem.toString()
            val priority = binding.prioritySpinner.selectedItem.toString()

            if (title.isNotEmpty() && body.isNotEmpty()) {
                createNote(title, body, category, priority, dueDate)
            } else {
                Toast.makeText(this@CreateNoteActivity, "Please fill out title and body", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            // After date is selected, show the TimePickerDialog
            showTimePickerDialog(selectedYear, selectedMonth, selectedDay)
        }, year, month, day)

        datePickerDialog.show()
    }

    @SuppressLint("DefaultLocale")
    private fun showTimePickerDialog(year: Int, month: Int, day: Int) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            // Format the date and time as yyyy-MM-dd HH:mm
            val formattedDate = String.format(
                "%04d-%02d-%02d %02d:%02d",
                year,
                month + 1,
                day,
                selectedHour,
                selectedMinute
            )
            dueDate = formattedDate

            // Update the button text with the selected date and time
            dueDateButton.text = "Due: $formattedDate"
        }, hour, minute, true)

        timePickerDialog.show()
    }

    fun createNote(title: String, body: String, category: String, priority: String, dueDate: String) {
        val dueDateToSend = if (dueDate.isEmpty()) "2000-01-01 00:00" else dueDate
        val noteRequest = NoteRequest(
            title = title,
            body = body,
            category = category,
            priority = priority,
            due_date = dueDateToSend
        )

        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.createNote(
                    token = "Token $authToken",
                    noteRequest = noteRequest
                )
                if (response.isSuccessful)
                    Toast.makeText(this@CreateNoteActivity, "Note created successfully!", Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(this@CreateNoteActivity, "Unknown error.", Toast.LENGTH_SHORT).show()
                finish() // Close activity and go back
            } catch (e: HttpException) {
                Toast.makeText(this@CreateNoteActivity, "Failed: ${e.message()}", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                Toast.makeText(this@CreateNoteActivity, "Network Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
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
