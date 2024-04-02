package de.oelkers.firenote.controllers.overview

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.view.children
import androidx.core.view.size
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import de.oelkers.firenote.R
import de.oelkers.firenote.controllers.detail.ARG_NOTE
import de.oelkers.firenote.controllers.detail.NoteDetailsActivity
import de.oelkers.firenote.controllers.folder.edit.EditFolderFragment
import de.oelkers.firenote.models.Folder
import de.oelkers.firenote.models.Note
import de.oelkers.firenote.persistence.FolderRepository
import de.oelkers.firenote.util.AppBarActivity

class FolderOverviewActivity : AppBarActivity() {

    private lateinit var repository: FolderRepository
    private lateinit var viewPager: ViewPager2
    private val viewModel: FolderOverviewViewModel by viewModels { NoteViewModelFactory(repository) }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folder_overview)
        val fab = findViewById<FloatingActionButton>(R.id.new_note_button)
        viewPager = findViewById(R.id.notes_view_pager)
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        val newFolderButton = findViewById<MaterialButton>(R.id.new_folder_button)
        repository = FolderRepository(baseContext)
        val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), this::onDetailsFinish)
        fab.setOnClickListener { onNewNoteClick(launcher) }
        newFolderButton.setOnClickListener { onNewFolderClick() }
        viewPager.adapter = FolderAdapter(this, viewModel)
        viewModel.allFolders.observe(this) { viewPager.adapter!!.notifyDataSetChanged() }
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = viewModel.allFolders.value!![position].name
            tab.view.setOnLongClickListener {
                EditFolderFragment.newInstance(position).show(supportFragmentManager, "editFolder")
                true
            }
        }.attach()
    }

    override fun onPause() {
        super.onPause()
        repository.saveAllFolders(viewModel.allFolders.value!!)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val result = super.onCreateOptionsMenu(menu)
        val quickDeleteButton = menu?.findItem(R.id.quickDelete)
        quickDeleteButton?.setVisible(viewModel.isAnySelected())
        quickDeleteButton?.setOnMenuItemClickListener {
            onQuickDeleteClick()
            true
        }
        val searchButton = menu?.findItem(R.id.search_button)
        searchButton?.setVisible(true)
        searchButton?.setEnabled(true)
        val search = searchButton?.actionView as SearchView
        search.queryHint = resources.getString(R.string.search_notes_hint)
        search.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.setFilterValue(newText)
                return false
            }
        })
        return result
    }

    private fun onNewNoteClick(launcher: ActivityResultLauncher<Intent>) {
        val intent = Intent(this, NoteDetailsActivity::class.java)
        launcher.launch(intent)
    }

    private fun onNewFolderClick() {
        EditFolderFragment.newInstance().show(supportFragmentManager, "editFolder")
    }

    private fun onDetailsFinish(result: ActivityResult) {
        if (result.resultCode == RESULT_OK) {
            val newNote: Note = result.data!!.extras?.getParcelable(ARG_NOTE)!!
            val folderViewModel = viewModel.getViewModelFor(viewPager.currentItem)
            folderViewModel.addNote(newNote)
        }
    }

    private fun onQuickDeleteClick() {
        viewModel.deleteSelected()
        invalidateOptionsMenu()
    }
}
