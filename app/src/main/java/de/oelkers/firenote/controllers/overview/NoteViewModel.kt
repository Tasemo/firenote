package de.oelkers.firenote.controllers.overview

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asFlow
import de.oelkers.firenote.models.Note
import de.oelkers.firenote.persistence.NoteRepository
import kotlinx.coroutines.flow.filter
import java.util.*

class NoteViewModel(repository: NoteRepository) : ViewModel() {

    val allNotesLiveData = MutableLiveData<List<Note>>(repository.readAllNotes())
    val filteredNotesLiveData = MutableLiveData<List<Note>>(allNotesLiveData.value)
    val allNotes: List<Note>
        get() = allNotesLiveData.value!!
    val filteredNotes: List<Note>
        get() = filteredNotesLiveData.value!!

    fun addNote(note: Note) {
        perform { all, filtered ->
            all.add(note)
            filtered.add(note)
        }
    }

    fun deleteNote(index: Int) {
        perform { all, filtered ->
            val removed = filtered.removeAt(index)
            all.remove(removed)
        }
    }

    fun deleteNotes(indices: Iterable<Int>) {
        val sorted = indices.sortedDescending()
        perform { all, filtered ->
            for (index in sorted) {
                val removed = filtered.removeAt(index)
                all.remove(removed)
            }
        }
    }

    fun updateNote(note: Note, index: Int) {
        perform { all, filtered ->
            val allIndex = all.indexOf(filtered[index])
            filtered[index] = note
            all[allIndex] = note
        }
    }

    fun swapNotes(from: Int, to: Int) {
        perform { all, filtered ->
            val allFromIndex = all.indexOf(filtered[from])
            val allToIndex = all.indexOf(filtered[to])
            Collections.swap(filtered, from, to)
            Collections.swap(all, allFromIndex, allToIndex)
        }
    }

    fun filter(text: CharSequence?) {
        if (text == null) {
            filteredNotesLiveData.postValue(allNotes)
        } else {
            val filtered = allNotes.filter { it.title?.contains(text) ?: false || it.content?.contains(text) ?: false }
            filteredNotesLiveData.postValue(filtered)
        }
    }

    private fun perform(task: (all: MutableList<Note>, filtered: MutableList<Note>) -> Unit) {
        val newAll = allNotes.toMutableList()
        val newFiltered = filteredNotes.toMutableList()
        task(newAll, newFiltered)
        allNotesLiveData.postValue(newAll)
        filteredNotesLiveData.postValue(newFiltered)
    }
}

@Suppress("UNCHECKED_CAST")
class NoteViewModelFactory(private val repository: NoteRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NoteViewModel(repository) as T
    }
}
