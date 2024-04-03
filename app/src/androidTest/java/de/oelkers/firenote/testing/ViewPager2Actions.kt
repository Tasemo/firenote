package de.oelkers.firenote.testing

import android.view.View
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.IdlingResource.ResourceCallback
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf

fun scrollRight(smoothScroll: Boolean = true): ViewAction {
    return object : ViewPagerScrollAction() {
        override fun getDescription(): String {
            return "ViewPager2 move one page to the right"
        }

        override fun performScroll(viewPager: ViewPager2) {
            val current = viewPager.currentItem
            viewPager.setCurrentItem(current + 1, smoothScroll)
        }
    }
}

fun scrollLeft(smoothScroll: Boolean = true): ViewAction {
    return object : ViewPagerScrollAction() {
        override fun getDescription(): String {
            return "ViewPager2 move one page to the left"
        }

        override fun performScroll(viewPager: ViewPager2) {
            val current = viewPager.currentItem
            viewPager.setCurrentItem(current - 1, smoothScroll)
        }
    }
}

fun scrollToPage(page: Int, smoothScroll: Boolean = true): ViewAction {
    return object : ViewPagerScrollAction() {
        override fun getDescription(): String {
            return "ViewPager2 move to page"
        }

        override fun performScroll(viewPager: ViewPager2) {
            viewPager.setCurrentItem(page, smoothScroll)
        }
    }
}

private class ViewPager2IdleListener : OnPageChangeCallback(), IdlingResource {
    private var currentState = ViewPager2.SCROLL_STATE_IDLE
    private lateinit var idleCallback: ResourceCallback

    override fun registerIdleTransitionCallback(resourceCallback: ResourceCallback) {
        idleCallback = resourceCallback
    }

    override fun getName(): String {
        return "ViewPager2"
    }

    override fun isIdleNow(): Boolean {
        return currentState == ViewPager2.SCROLL_STATE_IDLE
    }

    override fun onPageScrollStateChanged(state: Int) {
        currentState = state
        if (currentState == ViewPager2.SCROLL_STATE_IDLE) {
            idleCallback.onTransitionToIdle()
        }
    }
}

private abstract class ViewPagerScrollAction : ViewAction {
    override fun getConstraints(): Matcher<View> {
        return allOf(isDisplayed(), isAssignableFrom(ViewPager2::class.java))
    }

    override fun perform(uiController: UiController, view: View) {
        val viewPager = view as ViewPager2
        val idleListener = ViewPager2IdleListener()
        viewPager.registerOnPageChangeCallback(idleListener)
        IdlingRegistry.getInstance().register(idleListener)
        try {
            uiController.loopMainThreadUntilIdle()
            performScroll(viewPager)
            uiController.loopMainThreadUntilIdle()
        } finally {
            IdlingRegistry.getInstance().unregister(idleListener)
            viewPager.unregisterOnPageChangeCallback(idleListener)
        }
    }

    protected abstract fun performScroll(viewPager: ViewPager2)
}
