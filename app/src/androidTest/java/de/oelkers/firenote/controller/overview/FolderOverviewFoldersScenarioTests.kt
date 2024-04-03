package de.oelkers.firenote.controller.overview

import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.oelkers.firenote.R
import de.oelkers.firenote.controllers.overview.FolderOverviewActivity
import de.oelkers.firenote.testing.atTabLayoutPosition
import de.oelkers.firenote.testing.scrollRight
import org.hamcrest.CoreMatchers.not
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FolderOverviewFoldersScenarioTests {

    @Test
    fun testThatDefaultFolderIsPresent() {
        launchActivity<FolderOverviewActivity>().use {
            onView(atTabLayoutPosition(0)).perform(longClick())
            onView(withId(R.id.deleteButton)).perform(click())
            onView(withId(R.id.tab_layout)).check(matches(hasDescendant(withText("Default"))))
        }
    }

    @Test
    fun testThatFoldersCanBeCreatedAndDeleted() {
        launchActivity<FolderOverviewActivity>().use {
            onView(withId(R.id.new_folder_button)).perform(click())
            onView(withId(R.id.folder_name)).perform(typeText("Hello Folder"))
            onView(withId(R.id.saveButton)).perform(click())
            onView(withId(R.id.tab_layout)).check(matches(hasDescendant(withText("Hello Folder"))))
            onView(atTabLayoutPosition(1)).perform(longClick())
            onView(withId(R.id.deleteButton)).perform(click())
            onView(withId(R.id.tab_layout)).check(matches(not(hasDescendant(withText("Hello Folder")))))
        }
    }

    @Test
    fun testThatFoldersCanBeRenamed() {
        launchActivity<FolderOverviewActivity>().use {
            onView(atTabLayoutPosition(0)).perform(longClick())
            onView(withId(R.id.folder_name)).perform(replaceText("Hello Folder"))
            onView(withId(R.id.saveButton)).perform(click())
            onView(withId(R.id.tab_layout)).check(matches(hasDescendant(withText("Hello Folder"))))
        }
    }

    @Test
    fun testThatActiveLastFolderCanBeDeleted() {
        launchActivity<FolderOverviewActivity>().use {
            onView(withId(R.id.new_folder_button)).perform(click())
            onView(withId(R.id.folder_name)).perform(typeText("Hello Folder"))
            onView(withId(R.id.saveButton)).perform(click())
            onView(withId(R.id.notes_view_pager)).perform(scrollRight())
            onView(atTabLayoutPosition(1)).perform(longClick())
            onView(withId(R.id.deleteButton)).perform(click())
            onView(withId(R.id.tab_layout)).check(matches(not(hasDescendant(withText("Hello Folder")))))
        }
    }

    @Test
    fun testThatPreviousFoldersCanBeDeletedIfMoreAreActive() {
        launchActivity<FolderOverviewActivity>().use {
            onView(withId(R.id.new_folder_button)).perform(click())
            onView(withId(R.id.folder_name)).perform(typeText("Hello Folder"))
            onView(withId(R.id.saveButton)).perform(click())
            onView(withId(R.id.notes_view_pager)).perform(scrollRight())
            onView(atTabLayoutPosition(0)).perform(longClick())
            onView(withId(R.id.deleteButton)).perform(click())
            onView(withId(R.id.tab_layout)).check(matches(not(hasDescendant(withText("Default")))))
        }
    }
}
