package de.oelkers.firenote.persistence

import android.content.Context
import de.oelkers.firenote.models.Note
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.io.File

class NoteRepositoryTest {

    @Test
    fun testThatNotesAreSavedToDisk(@TempDir fileDir: File) {
        val context = mock<Context> {
            on(it.filesDir).doReturn(fileDir)
        }
        val repository = NoteRepository(context)
        val notes = listOf(Note("Note1"), Note("Note2"))
        repository.saveAllNotes(notes)
        assertEquals(2, fileDir.resolve(NOTE_DIRECTORY).list()?.size)
        assertIterableEquals(notes, repository.readAllNotes())
    }

    @Test
    fun testThatAllNotesCanBeOverwritten(@TempDir fileDir: File) {
        val context = mock<Context> {
            on(it.filesDir).doReturn(fileDir)
        }
        val repository = NoteRepository(context)
        val notes = mutableListOf(Note("Note1"), Note("Note2"))
        repository.saveAllNotes(notes)
        notes.removeAt(1)
        repository.saveAllNotes(notes)
        assertEquals(1, fileDir.resolve(NOTE_DIRECTORY).list()?.size)
        assertIterableEquals(notes, repository.readAllNotes())
    }
}
