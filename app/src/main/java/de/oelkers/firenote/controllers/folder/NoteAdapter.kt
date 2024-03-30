package de.oelkers.firenote.controllers.folder

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
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
    private val viewModel: FolderViewModel,
    private val onClick: ((Int) -> Unit)? = null,
    private val onLongClick: ((Int) -> Unit)? = null,
    private val touchHelper: ItemTouchHelper? = null
) : ListAdapter<Note, NoteHolder>(NoteDiffCallback) {

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): NoteHolder {
        val noteView = LayoutInflater.from(viewGroup.context).inflate(R.layout.note_entry, viewGroup, false)
        val card = noteView.findViewById<MaterialCardView>(R.id.node_card)
        val holder = NoteHolder(card)
        card.setOnClickListener { onClick?.invoke(holder.layoutPosition) }
        card.setOnLongClickListener {
            card.isChecked = !card.isChecked
            onLongClick?.invoke(holder.layoutPosition)
            onLongClick != null
        }
        card.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_MOVE) {
                touchHelper?.startDrag(holder)
                touchHelper != null
            } else {
                false
            }
        }
        return holder
    }

    override fun getItemCount() = viewModel.filteredNotes.value!!.size

    override fun onBindViewHolder(holder: NoteHolder, position: Int) {
        holder.bind(viewModel.filteredNotes.value!![position])
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
