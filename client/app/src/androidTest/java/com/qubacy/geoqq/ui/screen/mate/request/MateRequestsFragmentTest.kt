package com.qubacy.geoqq.ui.screen.mate.request

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.runner.RunWith
import com.qubacy.geoqq.R
import org.junit.Test

@RunWith(AndroidJUnit4::class)
class MateRequestsFragmentTest {
    private lateinit var mMateRequestsFragmentScenarioRule: FragmentScenario<MateRequestsFragment>

    @Before
    fun setup() {
        mMateRequestsFragmentScenarioRule = launchFragmentInContainer(
            themeResId = R.style.Theme_Geoqq_Mates)
    }

    @Test
    fun allElementsInPlaceTest() {
        Espresso.onView(withId(R.id.requests_recycler_view))
            .check(ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    @Test
    fun allElementsEnabledTest() {
        Espresso.onView(withId(R.id.requests_recycler_view))
            .perform(ViewActions.swipeDown())
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
    }
}