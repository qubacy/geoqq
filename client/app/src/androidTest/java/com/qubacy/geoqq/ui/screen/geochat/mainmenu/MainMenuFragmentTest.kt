package com.qubacy.geoqq.ui.screen.geochat.mainmenu

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.qubacy.geoqq.R
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainMenuFragmentTest {
    private lateinit var mMainMenuFragmentScenarioRule: FragmentScenario<MainMenuFragment>

    @Before
    fun setup() {
        mMainMenuFragmentScenarioRule =
            launchFragmentInContainer<MainMenuFragment>(themeResId = R.style.Theme_Geoqq_GeoChat)
        mMainMenuFragmentScenarioRule.moveToState(Lifecycle.State.RESUMED)
    }

    @Test
    fun allElementsAreInPlaceTest() {
        Espresso.onView(ViewMatchers.withId(R.id.go_menu_option))
            .check(
                ViewAssertions.matches(
                    ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        Espresso.onView(ViewMatchers.withId(R.id.mates_menu_option))
            .check(
                ViewAssertions.matches(
                    ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        Espresso.onView(ViewMatchers.withId(R.id.profile_menu_option))
            .check(
                ViewAssertions.matches(
                    ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    @Test
    fun menuOptionsAreEnabledTest() {
        Espresso.onView(Matchers.allOf(
            ViewMatchers.isDescendantOfA(ViewMatchers.withId(R.id.go_menu_option)),
            ViewMatchers.withId(R.id.menu_option_button)))
            .perform(ViewActions.click())
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
        Espresso.onView(Matchers.allOf(
            ViewMatchers.isDescendantOfA(ViewMatchers.withId(R.id.mates_menu_option)),
            ViewMatchers.withId(R.id.menu_option_button)))
            .perform(ViewActions.click())
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
        Espresso.onView(Matchers.allOf(
            ViewMatchers.isDescendantOfA(ViewMatchers.withId(R.id.profile_menu_option)),
            ViewMatchers.withId(R.id.menu_option_button)))
            .perform(ViewActions.click())
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
    }
}