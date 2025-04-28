package com.example.notekeeper

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.notekeeper.databinding.ItemNoteBinding
import com.example.notekeeper.models.NoteResponse

class NotesAdapter(private val notes: List<NoteResponse>) :
    RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    inner class NoteViewHolder(val binding: ItemNoteBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.binding.titleTextView.text = note.title
        holder.binding.contentTextView.text = note.body
        // Optional: If you have a date field, bind it here
    }

    override fun getItemCount(): Int = notes.size
}
