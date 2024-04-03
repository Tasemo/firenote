package de.oelkers.firenote.testing

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.matcher.BoundedMatcher
import com.google.android.material.tabs.TabLayout
import org.hamcrest.Description
import org.hamcrest.Matcher

fun atRecyclerViewPosition(position: Int, itemMatcher: Matcher<View>): Matcher<View> {
    return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
        override fun describeTo(description: Description) {
            description.appendText("has item at position $position: ")
            itemMatcher.describeTo(description)
        }

        override fun matchesSafely(view: RecyclerView): Boolean {
            val viewHolder = view.findViewHolderForLayoutPosition(position) ?: return false
            return itemMatcher.matches(viewHolder.itemView)
        }
    }
}

fun atTabLayoutPosition(position: Int): Matcher<View> {
    return object : BoundedMatcher<View, TabLayout.TabView>(TabLayout.TabView::class.java) {
        override fun describeTo(description: Description) {
            description.appendText("has item at position $position: ")
        }

        override fun matchesSafely(view: TabLayout.TabView): Boolean {
            return view.tab?.position == position
        }
    }
}
