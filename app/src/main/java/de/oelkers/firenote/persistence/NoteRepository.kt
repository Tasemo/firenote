package de.oelkers.firenote.persistence

import android.content.Context
import android.content.Context.MODE_PRIVATE
import de.oelkers.firenote.Note

class NoteRepository(private val context: Context) {

    fun readAllNotes(): MutableList<Note> {
        val result = ArrayList<Note>()
        context.fileList().map { fileName ->
            context.openFileInput(fileName).bufferedReader().use { reader ->
                val title = reader.readLine()
                val content = reader.readText()
                result.add(Note(title, content))
            }
        }
        return result
    }

    fun saveAllNotes(notes: List<Note>) {
        notes.mapIndexed { index, note ->
            context.openFileOutput("note-$index", MODE_PRIVATE).bufferedWriter().use { writer ->
                writer.write(note.title)
                writer.newLine()
                writer.write(note.content)
            }
        }
    }
}
