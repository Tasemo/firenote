package de.oelkers.firenote.persistence

import android.content.Context
import de.oelkers.firenote.models.Folder
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

const val NOTE_DIRECTORY = "notes"
const val AUDIO_DIRECTORY = "audio"

class FolderRepository(private val context: Context) {

    init {
        context.filesDir.resolve(NOTE_DIRECTORY).mkdir()
        context.filesDir.resolve(AUDIO_DIRECTORY).mkdir()
    }

    fun readAllFolders(includeDefault: Boolean = false): ArrayList<Folder> {
        val result = ArrayList<Folder>()
        val files = context.filesDir.resolve(NOTE_DIRECTORY).listFiles() ?: return result
        files.forEach { file ->
            ObjectInputStream(file.inputStream()).use { stream ->
                result.add(stream.readObject() as Folder)
            }
        }
        if (result.isEmpty() && includeDefault) {
            result.add(Folder("Default"))
        }
        return result
    }

    fun saveAllFolders(notes: Iterable<Folder>) {
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
