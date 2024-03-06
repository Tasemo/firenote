package de.oelkers.firenote

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

const val TITLE_ARG = "NOTE_TITLE"
const val CONTENT_ARG = "NOTE_CONTENT"
const val NOTE_POSITION_ARG = "NOTE_POSITION"
const val NOTE_POSITION_NOT_FOUND = -1
const val RESULT_DELETED = Activity.RESULT_FIRST_USER + 1

class MainActivity : AppCompatActivity() {

    private val notes = arrayListOf(
        Note("Note1", "Hello World!"),
        Note("SuperLong", "This is a super long note where the text does not fit into one line!"),
        Note("AnotherNote", "How many notes are you writing?")
    )

    private lateinit var adapter: NoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), this::onDetailsFinish)
        findViewById<FloatingActionButton>(R.id.newNoteButton).setOnClickListener { onNewNoteClick(launcher) }
        adapter = NoteAdapter(notes) { position -> onNoteClick(position, launcher) }
        findViewById<RecyclerView>(R.id.notesView).adapter = adapter
    }

    private fun onNewNoteClick(launcher: ActivityResultLauncher<Intent>) {
        val intent = Intent(this, NoteDetailsActivity::class.java)
        launcher.launch(intent)
    }

    private fun onNoteClick(position: Int, launcher: ActivityResultLauncher<Intent>) {
        val intent = Intent(this, NoteDetailsActivity::class.java)
        intent.putExtra(NOTE_POSITION_ARG, position)
        intent.putExtra(TITLE_ARG, notes[position].title)
        intent.putExtra(CONTENT_ARG, notes[position].content)
        launcher.launch(intent)
    }

    private fun onDetailsFinish(result: ActivityResult) {
        val position = result.data!!.getIntExtra(NOTE_POSITION_ARG, NOTE_POSITION_NOT_FOUND)
        if (result.resultCode == Activity.RESULT_OK) {
            val title = result.data!!.getStringExtra(TITLE_ARG)!!
            val content = result.data!!.getStringExtra(CONTENT_ARG)!!
            if (position == NOTE_POSITION_NOT_FOUND) {
                notes.add(Note(title, content))
                adapter.notifyItemInserted(notes.size - 1)
            } else {
                notes[position].title = title
                notes[position].content = content
                adapter.notifyItemChanged(position)
            }
        } else if (result.resultCode == RESULT_DELETED) {
            notes.removeAt(position)
            adapter.notifyItemRemoved(position)
        }
    }
}
