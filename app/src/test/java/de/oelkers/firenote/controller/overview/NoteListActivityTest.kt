package de.oelkers.firenote.controller.overview

import android.app.Activity
import androidx.activity.result.ActivityResult
import de.oelkers.firenote.controllers.overview.NoteListActivity
import de.oelkers.firenote.testing.LocalTaskExecutorRule
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(LocalTaskExecutorRule::class)
class NoteListActivityTest {

    @Test
    fun testThatPrematureActivityReturnIsHandled() {
        val activity = NoteListActivity()
        val result = ActivityResult(Activity.RESULT_CANCELED, null)
        assertDoesNotThrow { activity.onDetailsFinish(result) }
    }
}
