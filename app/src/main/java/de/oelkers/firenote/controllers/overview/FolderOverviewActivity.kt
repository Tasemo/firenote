package de.oelkers.firenote.controllers.overview

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import de.oelkers.firenote.R
import de.oelkers.firenote.controllers.detail.ARG_NOTE
import de.oelkers.firenote.controllers.detail.NoteDetailsActivity
import de.oelkers.firenote.models.Note
import de.oelkers.firenote.persistence.FolderRepository
import de.oelkers.firenote.util.AppBarActivity

class FolderOverviewActivity : AppBarActivity() {

    private lateinit var repository: FolderRepository
    private lateinit var viewPager: ViewPager2
    private val viewModel: FolderOverviewViewModel by viewModels { NoteViewModelFactory(repository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folder_overview)
        val fab = findViewById<FloatingActionButton>(R.id.newNoteButton)
        viewPager = findViewById(R.id.notes_view_pager)
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        repository = FolderRepository(baseContext)
        val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), this::onDetailsFinish)
        fab.setOnClickListener { onNewNoteClick(launcher) }
        viewPager.adapter = FolderAdapter(this, viewModel)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = viewModel.allFolders.value!![position].name
        }.attach()
    }

    override fun onPause() {
        super.onPause()
        repository.saveAllFolders(viewModel.allFolders.value!!)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val result = super.onCreateOptionsMenu(menu)
//        val quickDeleteButton = menu?.findItem(R.id.quickDelete)
//        quickDeleteButton?.setVisible(selected.isNotEmpty())
//        quickDeleteButton?.setOnMenuItemClickListener {
//            onQuickDeleteClick()
//            true
//        }
        val searchButton = menu?.findItem(R.id.search_button)
        searchButton?.setVisible(true)
        searchButton?.setEnabled(true)
        val search = searchButton?.actionView as SearchView
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

    private fun onDetailsFinish(result: ActivityResult) {
        if (result.resultCode == RESULT_OK) {
            val newNote: Note = result.data!!.extras?.getParcelable(ARG_NOTE)!!
            val folderViewModel = viewModel.getViewModelFor(viewPager.currentItem)
            folderViewModel.addNote(newNote)
        }
    }

    private fun onQuickDeleteClick() {
//        viewModel.deleteNotes(selected)
//        selected.clear()
//        invalidateOptionsMenu()
    }
}