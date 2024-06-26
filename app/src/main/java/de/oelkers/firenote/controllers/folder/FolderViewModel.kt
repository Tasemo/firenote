package de.oelkers.firenote.controllers.folder

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import de.oelkers.firenote.controllers.overview.FolderOverviewViewModel
import de.oelkers.firenote.models.Note
import de.oelkers.firenote.util.filter
import de.oelkers.firenote.util.toggle
import java.util.*

class FolderViewModel(private val folderOverviewViewModel: FolderOverviewViewModel, var folderIndex: Int) : ViewModel() {

    private val isActive = MutableLiveData(true)
    private val allFolders = folderOverviewViewModel.allFolders.toggle(isActive)
    val allNotes = allFolders.map { it[folderIndex].notes }
    val filteredNotes = allNotes.filter(folderOverviewViewModel.filterValue, this::filterNotes)
    private val selected: MutableList<Int> = ArrayList()

    fun addNote(note: Note) {
        perform { it.add(note) }
    }

    fun deleteNote(index: Int) {
        perform {
            val allIndex = getAllNoteIndex(index)
            it.removeAt(allIndex)
        }
    }

    fun selectNote(index: Int) {
        if (selected.contains(index)) {
            selected.remove(index)
        } else {
            selected.add(index)
        }
    }

    fun isAnySelected(): Boolean {
        return selected.isNotEmpty()
    }

    fun deleteSelected() {
        val sorted = selected.sortedDescending()
        perform {
            for (index in sorted) {
                val allIndex = getAllNoteIndex(index)
                it.removeAt(allIndex)
            }
        }
        selected.clear()
    }

    fun updateNote(note: Note, index: Int) {
        perform {
            val allIndex = getAllNoteIndex(index)
            it[allIndex] = note
        }
    }

    fun swapNotes(from: Int, to: Int) {
        perform {
            val allFromIndex = getAllNoteIndex(from)
            val allToIndex = getAllNoteIndex(to)
            Collections.swap(it, allFromIndex, allToIndex)
        }
    }

    fun deactivate() {
        isActive.value = false
    }

    fun activate() {
        isActive.value = true
    }

    private fun getAllNoteIndex(filteredIndex: Int): Int {
        val note = filteredNotes.value!![filteredIndex]
        return allNotes.value!!.indexOf(note)
    }

    private fun perform(task: (all: MutableList<Note>) -> Unit) {
        val folder = folderOverviewViewModel.allFolders.value!![folderIndex]
        task(folder.notes)
        folderOverviewViewModel.updateFolder(folder, folderIndex)
    }

    private fun filterNotes(filter: CharSequence?, entry: Note): Boolean {
        if (filter == null) {
            return true
        }
        return entry.title?.contains(filter) ?: false || entry.content?.contains(filter) ?: false
    }
}
