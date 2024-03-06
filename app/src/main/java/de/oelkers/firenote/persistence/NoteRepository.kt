package de.oelkers.firenote.persistence

import android.content.Context
import android.content.Context.MODE_PRIVATE
import de.oelkers.firenote.Note
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class NoteRepository(private val context: Context) {

    fun readAllNotes(): MutableList<Note> {
        val result = ArrayList<Note>()
        context.fileList().map { fileName ->
            ObjectInputStream(context.openFileInput(fileName)).use { stream ->
                result.add(stream.readObject() as Note)
            }
        }
        return result
    }

    fun saveAllNotes(notes: List<Note>) {
        notes.mapIndexed { index, note ->
            ObjectOutputStream(context.openFileOutput("note-$index", MODE_PRIVATE)).use { stream ->
                stream.writeObject(note)
            }
        }
    }
}
