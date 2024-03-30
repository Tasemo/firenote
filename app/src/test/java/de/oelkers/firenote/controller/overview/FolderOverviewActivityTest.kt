package de.oelkers.firenote.controller.overview

import androidx.activity.result.ActivityResult
import androidx.appcompat.app.AppCompatActivity
import de.oelkers.firenote.controllers.overview.FolderOverviewActivity
import de.oelkers.firenote.testing.LocalTaskExecutorRule
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(LocalTaskExecutorRule::class)
class FolderOverviewActivityTest {

    @Test
    fun testThatPrematureActivityReturnIsHandled() {
        val activity = FolderOverviewActivity()
        val result = ActivityResult(AppCompatActivity.RESULT_CANCELED, null)
        assertDoesNotThrow { activity.onDetailsFinish(result) }
    }
}
