package de.oelkers.firenote.controller.overview

import androidx.activity.viewModels
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import de.oelkers.firenote.R
import de.oelkers.firenote.controllers.overview.NoteHolder
import de.oelkers.firenote.controllers.overview.NoteListActivity
import de.oelkers.firenote.controllers.overview.NoteViewModel
import de.oelkers.firenote.controllers.overview.NoteViewModelFactory
import de.oelkers.firenote.models.Note
import de.oelkers.firenote.persistence.NoteRepository
import de.oelkers.firenote.testing.atPosition
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NoteListActivityIntegrationTest {

    @Test
    fun testThatQuickDeleteButtonIsVisibleIfItemSelected() {
        val repository = NoteRepository(InstrumentationRegistry.getInstrumentation().targetContext)
        val notes = listOf(Note("Note1"), Note("Note2"))
        repository.saveAllNotes(notes)
        launchActivity<NoteListActivity>().use {
            onView(withId(R.id.quickDelete)).check(doesNotExist())
            onView(withId(R.id.notesView)).perform(actionOnItemAtPosition<NoteHolder>(0, longClick()))
            onView(withId(R.id.quickDelete)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun testThatQuickDeleteButtonIsRemovedIfItemIsReselected() {
        val repository = NoteRepository(InstrumentationRegistry.getInstrumentation().targetContext)
        val notes = listOf(Note("Note1"), Note("Note2"))
        repository.saveAllNotes(notes)
        launchActivity<NoteListActivity>().use {
            onView(withId(R.id.notesView)).perform(actionOnItemAtPosition<NoteHolder>(0, longClick()))
            onView(withId(R.id.quickDelete)).check(matches(isDisplayed()))
            onView(withId(R.id.notesView)).perform(actionOnItemAtPosition<NoteHolder>(0, longClick()))
            onView(withId(R.id.quickDelete)).check(doesNotExist())
        }
    }

    @Test
    fun testThatItemsAreDeletedOnQuickDelete() {
        val repository = NoteRepository(InstrumentationRegistry.getInstrumentation().targetContext)
        val notes = listOf(Note("Note1"), Note("Note2"))
        repository.saveAllNotes(notes)
        launchActivity<NoteListActivity>().use {
            onView(withId(R.id.notesView)).perform(actionOnItemAtPosition<NoteHolder>(0, longClick()))
            onView(withId(R.id.notesView)).perform(actionOnItemAtPosition<NoteHolder>(1, longClick()))
            onView(withId(R.id.quickDelete)).perform(click())
            onView(withId(R.id.quickDelete)).check(doesNotExist())
            it.onActivity { activity ->
                val viewModel: NoteViewModel by activity.viewModels { NoteViewModelFactory(repository) }
                assertTrue(viewModel.allNotes.isEmpty())
            }
        }
    }

    @Test
    fun testThatItemsAreFilteredOnSearch() {
        val repository = NoteRepository(InstrumentationRegistry.getInstrumentation().targetContext)
        val notes = mutableListOf(Note("Note1", title = "TestTitle"), Note("Note2"), Note("Note3", content = "ContentTest"))
        repository.saveAllNotes(notes)
        launchActivity<NoteListActivity>().use {
            onView(withId(R.id.search_button)).perform(click())
            onView(withId(androidx.appcompat.R.id.search_src_text)).perform(typeText("Test"))
            onView(withId(R.id.notesView)).check(matches(atPosition(0, hasDescendant(withText("TestTitle")))))
            onView(withId(R.id.notesView)).check(matches(atPosition(1, hasDescendant(withText("ContentTest")))))
            it.onActivity { activity ->
                val viewModel: NoteViewModel by activity.viewModels { NoteViewModelFactory(repository) }
                assertEquals(notes, viewModel.allNotes)
                notes.removeAt(1)
                assertEquals(notes, viewModel.filteredNotes)
            }
        }
    }
}
