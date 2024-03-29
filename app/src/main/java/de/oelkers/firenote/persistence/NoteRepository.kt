package de.oelkers.firenote.persistence

import android.content.Context
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

    fun readAllNotes(): ArrayList<Note> {
        val result = ArrayList<Note>()
        val files = context.filesDir.resolve(NOTE_DIRECTORY).listFiles() ?: return result
        files.forEach { file ->
            ObjectInputStream(file.inputStream()).use { stream ->
                result.add(stream.readObject() as Note)
            }
        }
        return result
    }

    fun saveAllNotes(notes: Iterable<Note>) {
        deleteAllNotes()
        notes.forEachIndexed { index, note ->
            val fileName = context.filesDir.resolve(NOTE_DIRECTORY).resolve("note-$index")
            ObjectOutputStream(fileName.outputStream()).use { stream ->
                stream.writeObject(note)
            }
        }
    }

    private fun deleteAllNotes() {
        context.filesDir.resolve(NOTE_DIRECTORY).listFiles()?.forEach { file ->
            file.delete()
        }
    }
}
