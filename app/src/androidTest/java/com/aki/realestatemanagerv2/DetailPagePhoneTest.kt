package com.aki.realestatemanagerv2

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class DetailPagePhoneTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(ItemDetailHostActivity::class.java)

    @Test
    fun detailPagePhoneTest() {
        val recyclerView = onView(
            allOf(
                withId(R.id.item_list),
                childAtPosition(
                    withId(R.id.item_list_container),
                    0
                )
            )
        )
        recyclerView.perform(actionOnItemAtPosition<ViewHolder>(0, click()))

        val textView = onView(
            allOf(
                withId(R.id.detail_price), withText("$81,450,000.00"),
                withParent(withParent(withId(R.id.item_detail_scroll_view))),
                isDisplayed()
            )
        )
        textView.check(matches(withText("$81,450,000.00")))

        val textView2 = onView(
            allOf(
                withId(R.id.detail_address), withText("13 Woodstone Dr\n70471, Mandeville"),
                withParent(withParent(withId(R.id.item_detail_scroll_view))),
                isDisplayed()
            )
        )
        textView2.check(matches(withText("13 Woodstone Dr 70471, Mandeville")))

        val imageView = onView(
            allOf(
                withId(R.id.media_pic),
                withParent(withParent(withId(R.id.detail_media_rv))),
                isDisplayed()
            )
        )
        imageView.check(matches(isDisplayed()))

        val imageView2 = onView(
            allOf(
                withId(R.id.media_pic),
                withParent(withParent(withId(R.id.detail_media_rv))),
                isDisplayed()
            )
        )
        imageView2.check(matches(isDisplayed()))
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
