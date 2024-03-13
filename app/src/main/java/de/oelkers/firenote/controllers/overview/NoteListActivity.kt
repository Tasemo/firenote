package de.oelkers.firenote.controllers.overview

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.oelkers.firenote.R
import de.oelkers.firenote.controllers.detail.NoteDetailsActivity
import de.oelkers.firenote.models.Note
import de.oelkers.firenote.persistence.NoteRepository
import java.time.LocalDateTime

const val TITLE_ARG = "NOTE_TITLE"
const val CONTENT_ARG = "NOTE_CONTENT"
const val AUDIO_FILE_ARG = "NOTE_AUDIO_FILE"
const val NOTE_POSITION_ARG = "NOTE_POSITION"
const val NOTE_POSITION_NOT_FOUND = -1
const val RESULT_DELETED = Activity.RESULT_FIRST_USER + 1

class NoteListActivity : AppCompatActivity() {

    private lateinit var adapter: NoteAdapter
    private lateinit var repository: NoteRepository
    private val viewModel: NoteViewModel by viewModels { NoteViewModelFactory(repository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_list)
        repository = NoteRepository(baseContext)
        val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), this::onDetailsFinish)
        findViewById<FloatingActionButton>(R.id.newNoteButton).setOnClickListener { onNewNoteClick(launcher) }
        adapter = NoteAdapter(viewModel) { position -> onNoteClick(position, launcher) }
        viewModel.notesLiveData.observe(this, adapter::submitList)
        findViewById<RecyclerView>(R.id.notesView).adapter = adapter
    }

    override fun onPause() {
        super.onPause()
        repository.saveAllNotes(viewModel.notes)
    }

    private fun onNewNoteClick(launcher: ActivityResultLauncher<Intent>) {
        val intent = Intent(this, NoteDetailsActivity::class.java)
        launcher.launch(intent)
    }

    private fun onNoteClick(position: Int, launcher: ActivityResultLauncher<Intent>) {
        val intent = Intent(this, NoteDetailsActivity::class.java)
        val note = viewModel.notes[position]
        intent.putExtra(NOTE_POSITION_ARG, position)
        intent.putExtra(TITLE_ARG, note.title)
        intent.putExtra(CONTENT_ARG, note.content)
        intent.putExtra(AUDIO_FILE_ARG, note.audioPath)
        launcher.launch(intent)
    }

    private fun onDetailsFinish(result: ActivityResult) {
        val position = result.data!!.getIntExtra(NOTE_POSITION_ARG, NOTE_POSITION_NOT_FOUND)
        if (result.resultCode == Activity.RESULT_OK) {
            val title = result.data!!.getStringExtra(TITLE_ARG)
            val content = result.data!!.getStringExtra(CONTENT_ARG)
            val audioFile = result.data!!.getStringExtra(AUDIO_FILE_ARG)
            val newNote = Note(title, content, LocalDateTime.now(), audioFile)
            if (position == NOTE_POSITION_NOT_FOUND) {
                viewModel.addNote(newNote)
            } else {
                viewModel.updateNote(newNote, position)
            }
        } else if (result.resultCode == RESULT_DELETED) {
            if (position != NOTE_POSITION_NOT_FOUND) {
                viewModel.deleteNote(position)
            }
        }
    }
}
