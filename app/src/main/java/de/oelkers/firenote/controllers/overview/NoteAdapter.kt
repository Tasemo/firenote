package de.oelkers.firenote.controllers.overview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.oelkers.firenote.R
import de.oelkers.firenote.models.Note
import java.time.format.DateTimeFormatter

class NoteAdapter(
    private val notes: List<Note>,
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<NoteAdapter.NoteHolder>() {

    class NoteHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val title = view.findViewById<TextView>(R.id.titleText)
        private val content = view.findViewById<TextView>(R.id.contentText)
        private val created = view.findViewById<TextView>(R.id.createdText)
        private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

        fun bind(note: Note) {
            title.text = note.title
            content.text = note.content
            created.text = note.created?.format(dateFormatter)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): NoteHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.note_entry, viewGroup, false)
        val holder = NoteHolder(view)
        view.setOnClickListener { onClick(holder.layoutPosition) }
        return holder
    }

    override fun getItemCount() = notes.size

    override fun onBindViewHolder(holder: NoteHolder, position: Int) {
        holder.bind(notes[position])
    }
}
