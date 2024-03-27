package de.oelkers.firenote.controllers.overview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import de.oelkers.firenote.R
import de.oelkers.firenote.models.Note
import java.time.format.DateTimeFormatter

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

class NoteAdapter(
    private val viewModel: NoteViewModel,
    private val onClick: ((Int) -> Unit)? = null,
    private val onLongClick: ((Int) -> Unit)? = null
) : ListAdapter<Note, NoteHolder>(NoteDiffCallback) {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): NoteHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.note_entry, viewGroup, false)
        val card = view.findViewById<MaterialCardView>(R.id.node_card)
        val holder = NoteHolder(view)
        view.setOnClickListener { onClick?.invoke(holder.layoutPosition) }
        view.setOnLongClickListener {
            card.isChecked = !card.isChecked
            onLongClick?.invoke(holder.layoutPosition)
            onLongClick != null
        }
        return holder
    }

    override fun getItemCount() = viewModel.notes.size

    override fun onBindViewHolder(holder: NoteHolder, position: Int) {
        holder.bind(viewModel.notes[position])
    }

    companion object NoteDiffCallback : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }
    }
}
