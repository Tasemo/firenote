package de.oelkers.firenote.controllers.overview

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.oelkers.firenote.models.Note
import de.oelkers.firenote.persistence.NoteRepository
import java.util.*

class NoteViewModel(repository: NoteRepository) : ViewModel() {

    val notesLiveData = MutableLiveData<List<Note>>(repository.readAllNotes())
    val notes: List<Note>
        get() = notesLiveData.value!!

    fun addNote(note: Note) {
        perform { it.add(note) }
    }

    fun deleteNote(index: Int) {
        perform { it.removeAt(index) }
    }

    fun deleteNotes(indices: Iterable<Int>) {
        val sorted = indices.sortedDescending()
        perform {
            for (index in sorted) {
                it.removeAt(index)
            }
        }
    }

    fun updateNote(note: Note, index: Int) {
        perform { it[index] = note }
    }

    fun swapNotes(from: Int, to: Int) {
        perform { Collections.swap(it, from, to) }
    }

    private fun perform(task: (list: MutableList<Note>) -> Unit) {
        val newList = notes.toMutableList()
        task(newList)
        notesLiveData.postValue(newList)
    }
}

@Suppress("UNCHECKED_CAST")
class NoteViewModelFactory(private val repository: NoteRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NoteViewModel(repository) as T
    }
}
