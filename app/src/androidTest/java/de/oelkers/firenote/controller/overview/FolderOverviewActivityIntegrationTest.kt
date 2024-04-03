package de.oelkers.firenote.controller.overview

import androidx.activity.viewModels
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.GeneralLocation
import androidx.test.espresso.action.GeneralSwipeAction
import androidx.test.espresso.action.Press
import androidx.test.espresso.action.Swipe
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import de.oelkers.firenote.R
import de.oelkers.firenote.controllers.folder.NoteHolder
import de.oelkers.firenote.controllers.overview.FolderOverviewActivity
import de.oelkers.firenote.controllers.overview.FolderOverviewViewModel
import de.oelkers.firenote.models.Folder
import de.oelkers.firenote.models.Note
import de.oelkers.firenote.persistence.FolderRepository
import de.oelkers.firenote.testing.atRecyclerViewPosition
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FolderOverviewActivityIntegrationTest {

    private lateinit var notes: MutableList<Note>
    private lateinit var folders: MutableList<Folder>

    @Before
    fun setupDefaultData() {
        val repository = FolderRepository(InstrumentationRegistry.getInstrumentation().targetContext)
        notes = mutableListOf(Note("Note1", title = "TestTitle"), Note("Note2", title = "Note2"), Note("Note3", content = "ContentTest"))
        folders = mutableListOf(Folder("Folder1", notes))
        repository.saveAllFolders(folders)
    }

    @Test
    fun testThatQuickDeleteButtonIsVisibleIfItemSelected() {
        launchActivity<FolderOverviewActivity>().use {
            onView(withId(R.id.quickDelete)).check(doesNotExist())
            onView(withId(R.id.notesView)).perform(actionOnItemAtPosition<NoteHolder>(0, longClick()))
            onView(withId(R.id.quickDelete)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun testThatQuickDeleteButtonIsRemovedIfItemIsReselected() {
        launchActivity<FolderOverviewActivity>().use {
            onView(withId(R.id.notesView)).perform(actionOnItemAtPosition<NoteHolder>(0, longClick()))
            onView(withId(R.id.quickDelete)).check(matches(isDisplayed()))
            onView(withId(R.id.notesView)).perform(actionOnItemAtPosition<NoteHolder>(0, longClick()))
            onView(withId(R.id.quickDelete)).check(doesNotExist())
        }
    }

    @Test
    fun testThatItemsAreDeletedOnQuickDelete() {
        launchActivity<FolderOverviewActivity>().use {
            for (i in notes.indices) {
                onView(withId(R.id.notesView)).perform(actionOnItemAtPosition<NoteHolder>(i, longClick()))
            }
            onView(withId(R.id.quickDelete)).perform(click())
            onView(withId(R.id.quickDelete)).check(doesNotExist())
            it.onActivity { activity ->
                val viewModel: FolderOverviewViewModel by activity.viewModels()
                val folderViewModel = viewModel.getViewModelFor(0)
                assertTrue(folderViewModel.allNotes.value!!.none())
            }
        }
    }

    @Test
    fun testThatItemsAreFilteredOnSearch() {
        launchActivity<FolderOverviewActivity>().use {
            onView(withId(R.id.search_button)).perform(click())
            onView(withId(androidx.appcompat.R.id.search_src_text)).perform(typeText("Test"))
            onView(withId(R.id.notesView)).check(matches(atRecyclerViewPosition(0, hasDescendant(withText("TestTitle")))))
            onView(withId(R.id.notesView)).check(matches(atRecyclerViewPosition(1, hasDescendant(withText("ContentTest")))))
            it.onActivity { activity ->
                val viewModel: FolderOverviewViewModel by activity.viewModels()
                val folderViewModel = viewModel.getViewModelFor(0)
                assertEquals(notes, folderViewModel.allNotes.value)
                notes.removeAt(1)
                assertEquals(notes, folderViewModel.filteredNotes.value)
            }
        }
    }

    @Test
    fun testThatNotesCanBeReordered() {
        launchActivity<FolderOverviewActivity>().use {
            val endCoordinates = GeneralLocation.translate(GeneralLocation.CENTER, 0.0f, 1.75f)
            val swipeAction = GeneralSwipeAction(Swipe.SLOW, GeneralLocation.CENTER, endCoordinates, Press.FINGER)
            onView(withId(R.id.notesView)).perform(actionOnItemAtPosition<NoteHolder>(0, swipeAction))
            onView(withId(R.id.notesView)).check(matches(atRecyclerViewPosition(0, hasDescendant(withText("Note2")))))
            onView(withId(R.id.notesView)).check(matches(atRecyclerViewPosition(1, hasDescendant(withText("TestTitle")))))
        }
    }
}
