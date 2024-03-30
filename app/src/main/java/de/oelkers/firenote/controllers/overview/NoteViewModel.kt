package de.oelkers.firenote.controllers.overview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.oelkers.firenote.models.Note
import de.oelkers.firenote.util.filter
import java.util.*

class NoteViewModel : ViewModel() {

    private val _filterValue = MutableLiveData<CharSequence?>()
    val filterValue: LiveData<CharSequence?>
        get() = _filterValue
    private val _allNotes = MutableLiveData<List<Note>>()
    val allNotes: LiveData<List<Note>>
        get() = _allNotes

    private val _filteredNotes = _allNotes.filter(_filterValue, this::filterNotes)
    val filteredNotes: LiveData<List<Note>>
        get() = _filteredNotes

    fun setAllNotes(notes: List<Note>) {
        _allNotes.value = notes
    }

    fun setFilterValue(text: CharSequence?) {
        _filterValue.value = text
    }

    fun addNote(note: Note) {
        perform { it.add(note) }
    }

    fun deleteNote(index: Int) {
        perform {
            val allIndex = it.indexOf(filteredNotes.value!![index])
            it.removeAt(allIndex)
        }
    }

    fun deleteNotes(indices: Iterable<Int>) {
        val sorted = indices.sortedDescending()
        perform {
            for (index in sorted) {
                val allIndex = it.indexOf(filteredNotes.value!![index])
                it.removeAt(allIndex)
            }
        }
    }

    fun updateNote(note: Note, index: Int) {
        perform {
            val allIndex = it.indexOf(filteredNotes.value!![index])
            it[allIndex] = note
        }
    }

    fun swapNotes(from: Int, to: Int) {
        perform {
            val allFromIndex = it.indexOf(filteredNotes.value!![from])
            val allToIndex = it.indexOf(filteredNotes.value!![to])
            Collections.swap(it, allFromIndex, allToIndex)
        }
    }

    private fun perform(task: (all: MutableList<Note>) -> Unit) {
        val newList = allNotes.value!!.toMutableList()
        task(newList)
        _allNotes.postValue(newList)
    }

    private fun filterNotes(filter: CharSequence?, entry: Note): Boolean {
        if (filter == null) {
            return true
        }
        return entry.title?.contains(filter) ?: false || entry.content?.contains(filter) ?: false
    }
}
