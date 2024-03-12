package de.oelkers.firenote.persistence

import android.content.Context
import android.content.Context.MODE_PRIVATE
import de.oelkers.firenote.models.Note
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

const val NOTE_DIRECTORY = "notes"
const val AUDIO_DIRECTORY = "audio"

class NoteRepository(private val context: Context) {

    init {
        context.filesDir.resolve(NOTE_DIRECTORY).mkdir()
        context.filesDir.resolve(AUDIO_DIRECTORY).mkdir()
    }

    fun readAllNotes(): MutableList<Note> {
        val result = ArrayList<Note>()
        val files = context.filesDir.resolve(NOTE_DIRECTORY).listFiles() ?: return result
        files.map { file ->
            ObjectInputStream(file.inputStream()).use { stream ->
                result.add(stream.readObject() as Note)
            }
        }
        return result
    }

    fun saveAllNotes(notes: List<Note>) {
        deleteAllNotes()
        notes.mapIndexed { index, note ->
            val fileName = context.filesDir.resolve(NOTE_DIRECTORY).resolve("note-$index")
            ObjectOutputStream(fileName.outputStream()).use { stream ->
                stream.writeObject(note)
            }
        }
    }

    private fun deleteAllNotes() {
        context.filesDir.resolve(NOTE_DIRECTORY).listFiles()?.map { file ->
            file.delete()
        }
    }
}
