package de.oelkers.firenote.controllers.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.oelkers.firenote.models.Note
import de.oelkers.firenote.persistence.NoteRepository

class NoteViewModel(repository: NoteRepository) : ViewModel() {

    val notes = MutableLiveData<List<Note>>(repository.readAllNotes())

    fun addNote(note: Note) {
        perform {
            it.add(note)
        }
    }

    fun deleteNote(index: Int) {
        perform {
            it.removeAt(index)
        }
    }

    fun updateNote(note: Note, index: Int) {
        perform {
            it[index] = note
        }
    }

    private fun perform(task: (list: MutableList<Note>) -> Unit) {
        val newList = notes.value?.toMutableList() ?: mutableListOf()
        task(newList)
        notes.postValue(newList)
    }
}

@Suppress("UNCHECKED_CAST")
class NoteViewModelFactory(private val repository: NoteRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NoteViewModel(repository) as T
    }
}
