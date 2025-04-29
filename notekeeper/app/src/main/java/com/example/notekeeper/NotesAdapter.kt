package com.example.notekeeper

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.notekeeper.databinding.ItemNoteBinding
import com.example.notekeeper.models.NoteResponse

class NotesAdapter(private val notes: List<NoteResponse>) :
    RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    inner class NoteViewHolder(val binding: ItemNoteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            // Set click listener on the entire item view (each note)
            binding.root.setOnClickListener {
                // Get the note data for the clicked item
                val note = notes[adapterPosition]

                // Start the EditNoteActivity and pass the note ID
                val intent = Intent(binding.root.context, EditNoteActivity::class.java)
                intent.putExtra("note_id", note.id) // Pass the note's ID to the edit screen
                binding.root.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]

        holder.binding.titleTextView.text = note.title

        // Priority handling
        when (note.priority) {
            1 -> {
                holder.binding.priorityIcon.visibility = View.VISIBLE
                holder.binding.titleTextView.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.black))
            }
            2 -> {
                holder.binding.priorityIcon.visibility = View.GONE
                holder.binding.titleTextView.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.black))
            }
            3 -> {
                holder.binding.priorityIcon.visibility = View.GONE
                holder.binding.titleTextView.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.darker_gray))
            }
        }

        // Category handling (icon based on category)
        val categoryIconRes = when (note.category) {
            "Personal" -> R.drawable.ic_personal
            "Work" -> R.drawable.ic_work
            "School" -> R.drawable.ic_school
            "Travel" -> R.drawable.ic_travel
            else -> R.drawable.ic_category_default
        }
        holder.binding.categoryIcon.setImageResource(categoryIconRes)
    }

    override fun getItemCount(): Int = notes.size
}
