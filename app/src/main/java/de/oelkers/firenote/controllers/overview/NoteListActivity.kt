package de.oelkers.firenote.controllers.overview

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.Menu
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.VisibleForTesting
import androidx.annotation.VisibleForTesting.Companion.PRIVATE
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.oelkers.firenote.R
import de.oelkers.firenote.controllers.detail.NoteDetailsActivity
import de.oelkers.firenote.models.Note
import de.oelkers.firenote.persistence.NoteRepository
import de.oelkers.firenote.util.AppBarActivity

const val NOTE_ARG = "NOTE_ARG"
const val NOTE_POSITION_ARG = "NOTE_POSITION_ARG"
const val NOTE_POSITION_NOT_FOUND = -1
const val RESULT_DELETED = Activity.RESULT_FIRST_USER + 1

class NoteListActivity : AppBarActivity() {

    private lateinit var adapter: NoteAdapter
    private lateinit var repository: NoteRepository
    private val viewModel: NoteViewModel by viewModels { NoteViewModelFactory(repository) }
    private val selected: MutableList<Int> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_list)
        repository = NoteRepository(baseContext)
        val fab = findViewById<FloatingActionButton>(R.id.newNoteButton)
        val notesView = findViewById<RecyclerView>(R.id.notesView)
        val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), this::onDetailsFinish)
        fab.setOnClickListener { onNewNoteClick(launcher) }
        val touchHelper = ItemTouchHelper(CardItemTouchHelperCallback(viewModel))
        adapter = NoteAdapter(viewModel, { position -> onNoteClick(position, launcher) }, this::onNoteLongClick, touchHelper)
        viewModel.notesLiveData.observe(this, adapter::submitList)
        notesView.adapter = adapter
        touchHelper.attachToRecyclerView(notesView)
    }

    override fun onPause() {
        super.onPause()
        repository.saveAllNotes(viewModel.notes)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val result = super.onCreateOptionsMenu(menu)
        val quickDeleteButton = menu?.findItem(R.id.quickDelete)
        quickDeleteButton?.setVisible(selected.isNotEmpty())
        quickDeleteButton?.setOnMenuItemClickListener {
            onQuickDeleteClick()
            true
        }
        return result
    }

    private fun onNewNoteClick(launcher: ActivityResultLauncher<Intent>) {
        val intent = Intent(this, NoteDetailsActivity::class.java)
        launcher.launch(intent)
    }

    private fun onNoteClick(position: Int, launcher: ActivityResultLauncher<Intent>) {
        val intent = Intent(this, NoteDetailsActivity::class.java)
        val note = viewModel.notes[position]
        intent.putExtra(NOTE_POSITION_ARG, position)
        intent.putExtra(NOTE_ARG, note as Parcelable)
        launcher.launch(intent)
    }

    @VisibleForTesting(otherwise = PRIVATE)
    internal fun onDetailsFinish(result: ActivityResult) {
        val position = result.data?.getIntExtra(NOTE_POSITION_ARG, NOTE_POSITION_NOT_FOUND) ?: NOTE_POSITION_NOT_FOUND
        if (result.resultCode == Activity.RESULT_OK) {
            val newNote: Note = result.data!!.extras?.getParcelable(NOTE_ARG)!!
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

    private fun onNoteLongClick(position: Int) {
        if (selected.contains(position)) {
            selected.remove(position)
        } else {
            selected.add(position)
        }
        invalidateOptionsMenu()
    }

    private fun onQuickDeleteClick() {
        viewModel.deleteNotes(selected)
        selected.clear()
        invalidateOptionsMenu()
    }
}
