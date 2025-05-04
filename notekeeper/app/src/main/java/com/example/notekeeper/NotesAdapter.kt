package com.example.notekeeper

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.notekeeper.databinding.ItemNoteBinding
import com.example.notekeeper.models.NoteResponse

// NotesAdapter is responsible for displaying a list of notes in a RecyclerView.
// It binds each note to a view in the RecyclerView and handles interactions like clicking on a note.
class NotesAdapter(private val notes: List<NoteResponse>) :
    RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    // NoteViewHolder holds the reference to the views that will display each note item.
    inner class NoteViewHolder(val binding: ItemNoteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            // Set a click listener on the entire item view (each note).
            binding.root.setOnClickListener {
                // Get the note data for the clicked item based on its position in the adapter
                val note = notes[adapterPosition]

                // Create an Intent to start the EditNoteActivity, passing the note's ID as an extra
                val intent = Intent(binding.root.context, EditNoteActivity::class.java)
                intent.putExtra("note_id", note.id) // Pass the note's ID to the edit screen
                binding.root.context.startActivity(intent) // Start EditNoteActivity
            }
        }
    }

    // onCreateViewHolder is called when a new ViewHolder is created.
    // It inflates the layout for each note item and returns a NoteViewHolder.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        // Inflate the layout for each note item using ItemNoteBinding
        val binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding) // Return the ViewHolder with the inflated view
    }

    // onBindViewHolder is called to bind data to a specific item in the RecyclerView.
    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        // Get the note at the current position in the list
        val note = notes[position]

        // Set the title of the note in the titleTextView
        holder.binding.titleTextView.text = note.title

        // Priority handling:
        // Set the visibility of the priority icon based on the note's priority level.
        // Also, adjust the title text color based on the priority level.
        when (note.priority) {
            1 -> {
                holder.binding.priorityIcon.visibility = View.VISIBLE // Show icon for highest priority
                holder.binding.titleTextView.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.black))
            }
            2 -> {
                holder.binding.priorityIcon.visibility = View.GONE // Hide icon for medium priority
                holder.binding.titleTextView.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.black))
            }
            3 -> {
                holder.binding.priorityIcon.visibility = View.GONE // Hide icon for lowest priority
                holder.binding.titleTextView.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.darker_gray))
            }
        }

        // Category handling:
        // Set the appropriate icon based on the note's category (e.g., Personal, Work, School, Travel).
        // If the category is unknown, use a default category icon.
        val categoryIconRes = when (note.category) {
            "Personal" -> R.drawable.ic_personal
            "Work" -> R.drawable.ic_work
            "School" -> R.drawable.ic_school
            "Travel" -> R.drawable.ic_travel
            else -> R.drawable.ic_category_default
        }
        // Set the category icon for the note item
        holder.binding.categoryIcon.setImageResource(categoryIconRes)
    }

    // getItemCount returns the total number of items (notes) in the list
    override fun getItemCount(): Int = notes.size
}
