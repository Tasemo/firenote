package de.oelkers.firenote.persistence

import android.content.Context
import de.oelkers.firenote.models.Folder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.io.File

class FolderRepositoryTest {

    @Test
    fun testThatNotesAreSavedToDisk(@TempDir fileDir: File) {
        val context = mock<Context> {
            on(it.filesDir).doReturn(fileDir)
        }
        val repository = FolderRepository(context)
        val folders = listOf(Folder("Folder1"), Folder("Folder2"))
        repository.saveAllFolders(folders)
        assertEquals(2, fileDir.resolve(NOTE_DIRECTORY).list()?.size)
        assertIterableEquals(folders, repository.readAllFolders())
    }

    @Test
    fun testThatAllNotesCanBeOverwritten(@TempDir fileDir: File) {
        val context = mock<Context> {
            on(it.filesDir).doReturn(fileDir)
        }
        val repository = FolderRepository(context)
        val folders = mutableListOf(Folder("Folder1"), Folder("Folder2"))
        repository.saveAllFolders(folders)
        folders.removeAt(1)
        repository.saveAllFolders(folders)
        assertEquals(1, fileDir.resolve(NOTE_DIRECTORY).list()?.size)
        assertIterableEquals(folders, repository.readAllFolders())
    }
}
