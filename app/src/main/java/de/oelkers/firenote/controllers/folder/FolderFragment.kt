package de.oelkers.firenote.controllers.folder

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import de.oelkers.firenote.R
import de.oelkers.firenote.controllers.detail.*
import de.oelkers.firenote.controllers.overview.FolderOverviewViewModel
import de.oelkers.firenote.models.Note

private const val ARG_FOLDER = "ARG_FOLDER"
private const val ARG_NOTE_POSITION = "ARG_NOTE_POSITION"
private const val NOTE_POSITION_NOT_FOUND = -1

class FolderFragment : Fragment() {

    private lateinit var viewModel: FolderViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_folder, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val notesView = view.findViewById<RecyclerView>(R.id.notesView)
        val folder = requireArguments().getInt(ARG_FOLDER)
        val notesViewModel: FolderOverviewViewModel by activityViewModels()
        viewModel = notesViewModel.getViewModelFor(folder)
        val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), this::onDetailsFinish)
        val touchHelper = ItemTouchHelper(NoteTouchHelperCallback(viewModel))
        val adapter = NoteAdapter(
            viewModel, { position -> onNoteClick(position, launcher) }, this::onNoteLongClick, touchHelper
        )
        viewModel.filteredNotes.observe(viewLifecycleOwner, adapter::submitList)
        notesView.adapter = adapter
        touchHelper.attachToRecyclerView(notesView)
    }

    private fun onNoteClick(position: Int, launcher: ActivityResultLauncher<Intent>) {
        val intent = Intent(context, NoteDetailsActivity::class.java)
        val note = viewModel.filteredNotes.value!![position]
        intent.putExtra(ARG_NOTE_POSITION, position)
        intent.putExtra(ARG_NOTE, note as Parcelable)
        launcher.launch(intent)
    }

    private fun onNoteLongClick(position: Int) {
        viewModel.selectNote(position)
        requireActivity().invalidateOptionsMenu()
    }

    private fun onDetailsFinish(result: ActivityResult) {
        val position = result.data?.getIntExtra(ARG_NOTE_POSITION, NOTE_POSITION_NOT_FOUND) ?: NOTE_POSITION_NOT_FOUND
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val newNote: Note = result.data!!.extras?.getParcelable(ARG_NOTE)!!
            viewModel.updateNote(newNote, position)
        } else if (result.resultCode == RESULT_DELETED) {
            viewModel.deleteNote(position)
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(folder: Int) = FolderFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_FOLDER, folder)
            }
        }
    }
}
